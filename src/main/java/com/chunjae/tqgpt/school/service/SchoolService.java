package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.dto.SchoolDTO;
import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.entity.SchoolDetail;
import com.chunjae.tqgpt.school.repository.SchoolDetailRepository;
import com.chunjae.tqgpt.school.repository.SchoolRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolDetailRepository schoolDetailRepository;

    @Value("${nice-admin-key}")
    private String apiKey;

    public Page<School> search(SchoolDTO.searchRequestDto requestDto) {
        Pageable pageable = PageRequest.of(Integer.parseInt(requestDto.getPage()) - 1, 10);

        String city = requestDto.getCityName();
        String district = requestDto.getStreetAddr();
        String searchOption = requestDto.getSearchOption();
        String searchValue = requestDto.getSearchValue();

        String addr = district.equals("전체") ? city : city + " " + district;
        if (!city.equals("전체")) {
            if (searchOption.equals("전체")) {
                return schoolRepository.findSchoolsByAddr(addr, searchValue, pageable);
            } else if (searchOption.equals("학교명")) {
                return schoolRepository.findAllByStreetAddrContainingAndSchoolNameContaining(addr, searchValue, pageable);
            } else if (searchOption.equals("학교주소")) {
                return schoolRepository.findSchoolsByAddrDetail(city, searchValue, pageable);
            } else {
                return schoolRepository.findAllByStreetAddrContainingAndUserName(city, searchValue, pageable);
            }
        } else {
            if (searchOption.equals("전체")) {
              return schoolRepository.findAllBySchoolNameContainingOrStreetAddrContainingOrUserName(searchValue, searchValue, searchValue, pageable);
            } else if (searchOption.equals("학교명")) {
                return schoolRepository.findAllBySchoolNameContaining(searchValue, pageable);
            } else if (searchOption.equals("학교주소")) {
                return schoolRepository.findAllByStreetAddrContaining(searchValue, pageable);
            } else {
                return schoolRepository.findAllByUserName(searchValue, pageable);
            }
        }
    }

    public School getSchoolById(Long id) {
        return schoolRepository.getReferenceById(id);
    }

    public SchoolDetail getSchoolDetailById(Long id) {
        return schoolDetailRepository.getReferenceById(id);
    }

    @Transactional
    public void upsertSchoolData(String userName) {
        deleteApiData(userName);

        int pageIndex = 1;
        while (true) {
            try {
                JsonObject jsonObject = fetchSchoolData(pageIndex);
                log.info(String.valueOf(pageIndex));
                if (jsonObject == null) {
                    log.info("학교기본정보 API FINISH");
                    return;
                }

                processSchoolData(jsonObject, userName);
                pageIndex++;

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private void deleteApiData(String userName) {
        List<School> schools = schoolRepository.findByUserName(userName);
        for (School school : schools) {
            SchoolDetail detail = schoolDetailRepository.findById(school.getIdx()).orElse(null);
            if (detail != null) {
                schoolDetailRepository.delete(detail);
            }
        }
    }

    private JsonObject fetchSchoolData(int pageIndex) throws IOException {
        String apiUrl = buildApiUrl(pageIndex);
        String jsonResult = makeApiRequest(apiUrl);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResult, JsonObject.class);
        JsonArray schoolInfoArray = jsonObject.getAsJsonArray("schoolInfo");

        if (schoolInfoArray != null) {
            return schoolInfoArray.get(1).getAsJsonObject();
        }
        return null;
    }

    private String buildApiUrl(int pageIndex) {
        StringBuilder urlParametersBuilder = new StringBuilder()
                .append("&Type=json")
                .append("&SCHUL_KND_SC_NM=").append(URLEncoder.encode("고등학교", StandardCharsets.UTF_8))
                .append("&pSize=1000")
                .append("&pIndex=").append(pageIndex);

        return new StringBuilder()
                .append("https://open.neis.go.kr/hub/schoolInfo?KEY=")
                .append(apiKey)
                .append(urlParametersBuilder)
                .toString();
    }

    private String makeApiRequest(String apiUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (BufferedReader br = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) ?
                new BufferedReader(new InputStreamReader(conn.getInputStream())) :
                new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    private void processSchoolData(JsonObject schoolInfo, String userName) {
        JsonArray rowArray = schoolInfo.getAsJsonArray("row");

        for (JsonElement rowElement : rowArray) {
            School school = createSchool(rowElement.getAsJsonObject(), userName);
            SchoolDetail schoolDetail = createSchoolDetail(rowElement.getAsJsonObject(), school);

            schoolRepository.save(school);
            schoolDetailRepository.save(schoolDetail);
        }
    }

    private School createSchool(JsonObject row, String userName) {
        String cityName = row.get("LCTN_SC_NM").getAsString();
        String streetAddr = row.get("ORG_RDNMA").getAsString();
        String schoolKind = row.get("SCHUL_KND_SC_NM").getAsString();
        String schoolName = row.get("SCHUL_NM").getAsString();
        String cityEduOrg = row.get("ATPT_OFCDC_SC_NM").getAsString();
        String localEduOrg = row.get("JU_ORG_NM").getAsString();

        return new School(cityName, streetAddr, schoolKind, schoolName, cityEduOrg, localEduOrg, userName);
    }

    private SchoolDetail createSchoolDetail(JsonObject row, School school) {
        String schoolCode = row.get("SD_SCHUL_CODE").getAsString();
        String foundationName = row.get("FOND_SC_NM").getAsString();
        String dayNightName = row.get("DGHT_SC_NM").getAsString();
        String streetDetailAddr = row.get("ORG_RDNDA").getAsString();
        String postNum = row.get("ORG_RDNZC").getAsString();
        String telNum = row.get("ORG_TELNO").getAsString();
        String hmpgAddr = row.get("HMPG_ADRES").isJsonNull() ? "" : row.get("HMPG_ADRES").getAsString();
        String faxNum = row.get("ORG_FAXNO").isJsonNull() ? "" : row.get("ORG_FAXNO").getAsString();
        String coedu = row.get("COEDU_SC_NM").getAsString();

        return new SchoolDetail(school, schoolCode, foundationName, dayNightName, streetDetailAddr, postNum, telNum, hmpgAddr, faxNum, coedu);
    }


    public SchoolDetail addSchool(SchoolDTO.SchoolAddDto schoolAddDto, String userName) {
        SchoolDetail addSchoolInfo = null;
        try {
            addSchoolInfo = schoolAddDto.toEntity(userName);
            schoolDetailRepository.save(addSchoolInfo);

        } catch (Exception e) {
            log.info("addSchool exception");
        }
        return addSchoolInfo;

    }


    public Optional<SchoolDetail> getSchoolOne(Long SchoolIdx) {
        return schoolDetailRepository.findById(SchoolIdx);
    }

    @Transactional
    public ResponseEntity<SchoolDetail> modifySchool(SchoolDTO.SchoolModifyDto dto, String userName) {
        Optional<School> getSchool = schoolRepository.findById(dto.getSchoolIdx());
        Optional<SchoolDetail> getSchoolDetail = schoolDetailRepository.findById(dto.getSchoolIdx());

        School modifySchool = null;
        SchoolDetail modifySchoolDetail = null;

        if (getSchool.isEmpty() || getSchoolDetail.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        modifySchool = getSchool.get();
        modifySchoolDetail = getSchoolDetail.get();

        modifySchool.update(dto, userName);
        modifySchoolDetail.update(dto);

        return new ResponseEntity<>(modifySchoolDetail,HttpStatus.OK);
    }

    public List<School> findSchoolsByKeyword(String keyword) {
        return schoolRepository.findSchoolsByKeyword(keyword);
    }

    public void xlsxDownloadService(ByteArrayOutputStream bos, SchoolDTO.searchRequestDto requestDto) throws IOException {
        //excel sheet 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("학교_정보");    //파일명
        sheet.setDefaultColumnWidth(28);    //디폴트 너비

        //엑셀 폰트
        XSSFFont headerXSSFFont = (XSSFFont) workbook.createFont();
        headerXSSFFont.setColor(new XSSFColor((new byte[]{(byte) 255, (byte) 255, (byte) 255})));

        //엘셀 쉘
        XSSFCellStyle headerXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

        //테두리
        headerXssfCellStyle.setBorderLeft(BorderStyle.THIN);
        headerXssfCellStyle.setBorderRight(BorderStyle.THIN);
        headerXssfCellStyle.setBorderTop(BorderStyle.THIN);
        headerXssfCellStyle.setBorderBottom(BorderStyle.THIN);

        //배경
        headerXssfCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 37, (byte) 41}));
        headerXssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerXssfCellStyle.setFont(headerXSSFFont);

        //바디
        XSSFCellStyle bodyXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

        // 테두리 설정
        bodyXssfCellStyle.setBorderLeft(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderRight(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderTop(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderBottom(BorderStyle.THIN);

        int rowCount = 0;
        String headerNames[] = new String[]{"No", "시도명", "시군구명", "학교급", "학교명", "시도교육청명", "지역교육청명", "등록자", "등록일"};

        Row headerRow = null;
        Cell headerCell = null;

        headerRow = sheet.createRow(rowCount++);
        for (int i = 0; i < headerNames.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerNames[i]);    //데이터 추가
            headerCell.setCellStyle(headerXssfCellStyle);   //디자인 적용
        }

        List<School> allSchoolData = new ArrayList<>();
        int pageNumber = 0;
        Page<School> schoolPages;

        do {
            requestDto.setPage(String.valueOf(pageNumber + 1));
            schoolPages = search(requestDto);
            List<School> schools = schoolPages.getContent();
            allSchoolData.addAll(schools);
            pageNumber++;
        } while (schoolPages.hasNext());

        Row bodyRow = null;
        Cell bodyCell = null;

        for (School school : allSchoolData) {
            bodyRow = sheet.createRow(rowCount++);
            String[] bodyDatas = new String[]{
                    String.valueOf(school.getIdx()),  // 가정: getIdx()는 정수 ID를 반환
                    school.getCityName(),
                    school.getStreetAddr(),
                    school.getSchoolKind(),
                    school.getSchoolName(),
                    school.getCityEduOrg(),
                    school.getLocalEduOrg(),
                    school.getUserName(),
                    school.getCreatedAt().toString()  // 가정: getCreatedAt()는 java.util.Date 또는 java.time.LocalDateTime 등을 반환
            };

            for (int i = 0; i < bodyDatas.length; i++) {
                bodyCell = bodyRow.createCell(i);
                bodyCell.setCellValue(bodyDatas[i]);  // 데이터 추가
                bodyCell.setCellStyle(bodyXssfCellStyle);  // 스타일 추가, 필요하다면
            }
        }

        workbook.write(bos);
        workbook.close();

    }

    public List<School> findTop5SchoolsByKeyword(String keyword) {
        return schoolRepository.findAllBySchoolNameContaining(keyword).stream().limit(5).toList();
    }

    public School searchSchoolOne(String schoolName) {
        return schoolRepository.findSchoolBySchoolName(schoolName);
    }
}

