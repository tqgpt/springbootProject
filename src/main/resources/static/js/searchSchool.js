document.addEventListener("DOMContentLoaded", () => {
    const area = $("select[name^=city]");
    const district = $("select[name^=district]");

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
            if (selectedCity !== "") {
                const cities = selectedArea.cities; // 구/군
                cities.forEach((item) => {
                    district.append(`<option value="${item}">${item}</option>`);
                });
            } else {
                district.append('<option value="">구/군</option>');
            }
        });
    });

    // 초기화 버튼 클릭 시
    document.getElementById('resetBtn').addEventListener('click', function (event) {
        event.preventDefault(); // 버튼의 기본 동작 막기

        // 폼 필드 초기화
        document.getElementById('city').value = "";
        document.getElementById('district').value = "";
        document.querySelector('.form-select').value = "all";
        document.querySelector('.form-control').value = "";

        // 검색 결과 지우기 (원하는 대상에 맞게 수정)
        const tableBody = document.getElementById('tableBody');
        searchSchool();
    });
});

let ex_city;
let ex_district;
let ex_search_option;
let ex_search_option_value;

//학교 데이터 출력
const searchSchool = (pageNumber) => {
    const tableBody = document.getElementById('tableBody');
    const url = getSearchURL(pageNumber);
    console.log(url)
    fetch(url)
        .then(response => response.json())
        .then(data => {
            tableBody.innerHTML = '';
            data.forEach((school) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                            <th>${school.idx}</th>
                            <td>${school.cityName}</td>
                            <td>${school.streetAddr}</td>
                            <td>${school.schoolKind}</td>
                            <td>${school.schoolName}</td>
                            <td>${school.cityEduOrg}</td>
                            <td>${school.localEduOrg}</td>
                            <td>${school.userName}</td>
                            <td>${school.createdAt}</td>
                        `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            const row = document.createElement('tr');
            row.innerHTML = `<th colspan="9" class="text-center" style="height: 100px">학교를 찾을 수 없어요</th>`;
            tableBody.appendChild(row);
        });
}

// 검색 조건
const getSearchURL = (pageNumber) => {
    const city = ex_city || document.getElementById('city').value;
    const district = ex_district || document.getElementById('district').value;
    const searchOption = ex_search_option || document.querySelector('#searchOption').value;
    const searchValue = ex_search_option_value || document.querySelector('#searchValue').value;

    let url = '/high/school/search-list';
    const params = [];

    params.push(`city=${city}`);
    params.push(`district=${district}`);
    params.push(`option=${searchOption}&value=${searchValue}`);
    params.push(`page=${pageNumber}`);

    url += '?' + params.join('&');

    return url;
}

//페이징
const pageLinks = document.querySelectorAll('.page-link');
pageLinks.forEach(pageLink => {
    pageLink.addEventListener('click', (event) => {
        event.preventDefault();
        const pageNumber = pageLink.getAttribute('data-page');
        if (pageNumber) {
            document.querySelectorAll('.page-item').forEach(pageItem => {
                pageItem.classList.remove('active');
            });
            pageLink.closest('.page-item').classList.add('active');

            // 검색 함수 호출
            searchSchool(pageNumber);
        }
    });
});

// 검색 버튼 클릭 시
document.getElementById('searchBtn').addEventListener('click', function (event) {
    event.preventDefault(); // 버튼의 기본 동작 막기

    // 검색 조건 업데이트
    ex_city = document.getElementById('city').value;
    ex_district = document.getElementById('district').value;
    ex_search_option = document.querySelector('#searchOption').value;
    ex_search_option_value = document.querySelector('#searchValue').value;

    // 검색 함수 호출
    searchSchool(1);

    // 페이지 번호 업데이트: 1페이지로 이동
    document.querySelectorAll('.page-item').forEach(pageItem => {
        pageItem.classList.remove('active');
    });
    document.querySelector('.page-link[data-page="1"]').closest('.page-item').classList.add('active');
});

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

// 데이터 테이블 라이브러리
$('#tb').DataTable({
    lengthChange: false,
    searching: false,
    info: false,
    paging: false,
    language: {emptyTable: "학교를 찾을 수 없어요"},
});

//toast UI
const toastTrigger = document.getElementById('liveToastBtn')
const toastLiveExample = document.getElementById('liveToast')
if (toastTrigger) {
    const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastLiveExample)
    toastTrigger.addEventListener('click', () => {
        toastBootstrap.show()
    })
}