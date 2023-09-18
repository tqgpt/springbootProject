let ex_cityName = sessionStorage.getItem('cityName') || '전체';           // 시/도
let ex_streetAddr = sessionStorage.getItem('streetAddr') || '전체';       // 구/군
let ex_search_option = sessionStorage.getItem('search_option') || '전체'; // 검색 조건
let ex_search_value = sessionStorage.getItem('search_value') || '';      // 검색 입력 값
let page_number = sessionStorage.getItem('pageNumber') || '1';          // 페이지 번호

window.onload = () => {
    sessionStorage.removeItem('cityName');
    sessionStorage.removeItem('streetAddr');
    sessionStorage.removeItem('search_option');
    sessionStorage.removeItem('search_value');
    sessionStorage.removeItem('pageNumber');
}


document.addEventListener("DOMContentLoaded", () => {
    searchSchool(page_number);
    const area = $("select[name^=cityName]");
    const district = $("select[name^=streetAddr]");

    $.getJSON("/json/city.json", (data) => {
        const cityData = data.area;
        const citiesData = data;

        cityData.forEach((item) => {
            area.append(`<option value="${item.name}">${item.name}</option>`);
        });

        area.change(function () {
            const selectedCity = $(this).val();
            const selectedArea = citiesData.area.find((item) => item.name === selectedCity); // 시/도

            district.find("option").remove();
            if (selectedCity !== "전체") {
                const cities = selectedArea.cities; // 구/군
                cities.forEach((item) => {
                    district.append(`<option value="${item}">${item}</option>`);
                });
            } else {
                district.append('<option value="전체">구/군</option>');
            }
        });
    });

    // 초기화 버튼 클릭 시
    document.getElementById('resetBtn').addEventListener('click', function (event) {
        // 폼 필드 초기화
        document.getElementById('cityName').value = "전체";
        document.getElementById('streetAddr').innerHTML=`<option value="전체">구/군</option>`
        document.getElementById('searchOption').value = "전체"
        document.getElementById('searchValue').value = "";

        searchFirstPage();
    });
});

const remindKeywords = () => {
    const cityNameSelect = document.getElementById('cityName');
    const streetAddrSelect = document.getElementById('streetAddr');

    for (let i = 0; i < cityNameSelect.options.length; i++) {
        if (cityNameSelect.options[i].value === ex_cityName) {
            cityNameSelect.options[i].selected = true;
            break;
        }
    }

    for (let i = 0; i < streetAddrSelect.options.length; i++) {
        if (streetAddrSelect.options[i].value === ex_streetAddr) {
            streetAddrSelect.options[i].selected = true;
            break;
        }
    }
}


const toggleTab = (tabName) => {
    const tabs = document.querySelectorAll('.nav-tabs .nav-link');
    tabs.forEach(tab => {
        if (tab.textContent === tabName) {
            tab.classList.add('active');
        } else {
            tab.classList.remove('active');
        }
    });
}

//toast UI
const toastTrigger = document.getElementById('liveToastBtn')
const toastLiveExample = document.getElementById('liveToast')
if (toastTrigger) {
    const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastLiveExample)
    toastTrigger.addEventListener('click', () => {
        toastBootstrap.show()
    })
}


/*
 * =========================================================검색=========================================================
 *
 * **/
// 검색 버튼 클릭 시
document.getElementById('searchBtn').addEventListener('click', function (event) {
    searchFirstPage()
});

const searchFirstPage = () => {
    event.preventDefault(); // 버튼의 기본 동작 막기

    // 검색 조건 업데이트
    ex_cityName = document.getElementById('cityName').value;
    ex_streetAddr = document.getElementById('streetAddr').value;
    ex_search_option = document.querySelector('#searchOption').value;
    ex_search_value = document.querySelector('#searchValue').value;

    // 검색 함수 호출
    searchSchool(1);

    // 페이지 번호 업데이트: 1페이지로 이동
    document.querySelectorAll('.page-item').forEach(pageItem => {
        pageItem.classList.remove('active');
    });

    const pageLink = document.querySelector('.page-link[data-page="1"]');
    if (pageLink) {
        const pageItem = pageLink.closest('.page-item');
        if (pageItem) {
            pageItem.classList.add('active');
        }
    }
}

// 검색 조건
const getParams = (pageNumber) => {
    return {
        cityName: ex_cityName !== "" ? ex_cityName : null,
        streetAddr: ex_streetAddr !== "" ? ex_streetAddr : null,
        searchOption: ex_search_option !== "" ? ex_search_option : null,
        searchValue: ex_search_value !== "" ? ex_search_value : "",
        page: pageNumber ? pageNumber.toString() : '1'
    };
}

// 페이징 변수
let totalCount = 0;
const pageCount = 5;
const showCount = 10;

const searchSchool = (pageNumber) => {
    const tableBody = document.getElementById('tableBody');
    const searchParams = getParams(pageNumber);

    fetch('/high/school/search-list', {
        method: 'POST', // POST 요청 사용
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(searchParams)
    }).then(response => response.json())
        .then(data => {
            const schoolList = data.contents.content;
            totalCount = data.count;
            const newTBody = document.createElement('tbody');
            newTBody.id = 'tableBody';

            schoolList.forEach((school) => {
                const row = document.createElement('tr');
                row.onclick = () => {
                    sessionStorage.setItem('cityName', ex_cityName);
                    sessionStorage.setItem('streetAddr', ex_streetAddr);
                    sessionStorage.setItem('search_option', ex_search_option);
                    sessionStorage.setItem('search_value', ex_search_value);
                    sessionStorage.setItem('pageNumber', pageNumber);
                    location.href=`/high/school/info/${school.idx}`;
                };

                const dateObj = new Date(school.createdAt);
                const formattedDate = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;

                row.innerHTML = `
                    <th>${school.idx}</th>
                    <td>${school.cityName}</td>
                    <td class="text-start">${school.streetAddr}</td>
                    <td>${school.schoolKind}</td>
                    <td class="text-start">${school.schoolName}</td>
                    <td>${school.cityEduOrg}</td>
                    <td>${school.localEduOrg}</td>
                    <td>${school.userName}</td>
                    <td>${formattedDate}</td>
                `;
                newTBody.appendChild(row);
            });
            document.querySelector("span[name='total']").textContent = "총 " + totalCount + "개";
            tableBody.parentNode.replaceChild(newTBody, tableBody);
            generatePagination(Number(pageNumber)); // 페이지네이션 생성
        })
        .catch(error => {
            const newTBody = document.createElement('tbody');
            newTBody.id = 'tableBody';
            const row = document.createElement('tr');
            row.innerHTML = `<th colspan="9" class="text-center">학교를 찾을 수 없어요</th>`;
            newTBody.appendChild(row);
            tableBody.parentNode.replaceChild(newTBody, tableBody);
            generatePagination(0)
        });
}

// 페이징 처리
const generatePagination = (currentPage) => {
    let totalPage = Math.ceil(totalCount / showCount);
    let pageGroup = Math.ceil(currentPage / pageCount);

    let lastNumber = pageGroup * pageCount;
    if (lastNumber > totalPage) {
        lastNumber = totalPage;
    }

    let firstNumber = lastNumber > 5 ? lastNumber - (pageCount - 1) : 1;
    let paginationHTML = '';

    for (let i = firstNumber; i <= lastNumber; i++) {
        paginationHTML += `<li class="page-item ${i === currentPage ? 'active' : ''}"><label class="page-link" data-page="${i}" onclick="goPage(this)">${i}</label></li>`;
    }

    paginationHTML = `
    <li class="page-item"><a class="page-link" aria-label="Previous" onclick="goPrevious(${currentPage})"><label aria-hidden="true">이전</label></a></li>
    ${paginationHTML}
    <li class="page-item"><a class="page-link" aria-label="Next" onclick="goNext(${currentPage})"><label aria-hidden="true">다음</label></a></li>
  `;

    const paginationContainer = document.querySelector('.pagination-container');
    paginationContainer.innerHTML = paginationHTML;

    const prevButton = document.querySelector('.page-link[aria-label="Previous"]');
    const nextButton = document.querySelector('.page-link[aria-label="Next"]');

    if (pageGroup === 1) {
        prevButton.style.display = 'none'; // 첫 페이지 그룹이면 Previous 버튼 숨김
    } else {
        prevButton.style.display = 'block'; // 그 외에는 표시
    }

    if (pageGroup * pageCount >= totalPage) {
        nextButton.style.display = 'none'; // 마지막 페이지 그룹이면 Next 버튼 숨김
    } else {
        nextButton.style.display = 'block'; // 그 외에는 표시
    }
}


const goPage = (page) => {
    event.preventDefault();
    const pageNumber = page.getAttribute('data-page');
    if (pageNumber) {
        document.querySelectorAll('.page-item').forEach(pageItem => {
            pageItem.classList.remove('active');
        });
        page.closest('.page-item').classList.add('active');
        searchSchool(pageNumber);
    }
}

const goPrevious = (currentPage) => {
    event.preventDefault();
    currentPage = Math.ceil(currentPage / pageCount) * pageCount - pageCount;
    if (currentPage < 1) {
        currentPage = 1;
    }
    generatePagination();
    searchSchool(currentPage);
};

const goNext = (currentPage) => {
    event.preventDefault();
    const currentGroupLastPage = Math.ceil(currentPage / pageCount) * pageCount;

    currentPage = currentGroupLastPage + 1;
    if (currentPage > totalCount) {
        currentPage = totalPage;
    }
    generatePagination();
    searchSchool(currentPage);
};

const goFirst = () => {
    const currentPage = 1;
    generatePagination(1);
    searchSchool(currentPage);

    // 현재 페이지 그룹을 1로 설정하고 1번 페이지가 활성화되도록 처리
    document.querySelectorAll('.page-item').forEach(pageItem => {
        pageItem.classList.remove('active');
    });
    document.querySelector('.page-link[data-page="1"]').closest('.page-item').classList.add('active');
};

const goLast = () => {
    const currentPage = Math.ceil(totalCount / showCount);
    generatePagination(Math.ceil(totalCount / showCount),true);
    searchSchool(currentPage);

    document.querySelectorAll('.page-item').forEach(pageItem => {
        pageItem.classList.remove('active');
    });
    document.querySelector(`.page-link[data-page="${currentPage}"]`).closest('.page-item').classList.add('active');
};

//Excel
const ExcelDownloader = async () => {
    const currentTime = new Date();
    const time = currentTime.toLocaleString();
    var toastTime = document.querySelector('#liveToast .toast-header small');
    toastTime.innerText = time;

    const cityName = ex_cityName;
    const streetAddr = ex_streetAddr;
    const searchOption = ex_search_option;
    const searchValue = ex_search_value;

    const params = new URLSearchParams({
        cityName,
        streetAddr,
        searchOption,
        searchValue,
    });

    const url = '/high/school/xlsx-download?' + params.toString();

    const response = await fetch(url, {
        method: 'GET', // 메서드를 GET으로 설정
    });

    if (response.ok) {
        // 응답 처리 로직
        const blob = await response.blob();

        // Blob을 사용하여 다운로드 링크를 만듬
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = '학교_정보.xlsx'; // 원하는 파일명을 설정
        document.body.appendChild(a);
        a.click();
    } else {
        alert("다운로드 실패");
    }
}