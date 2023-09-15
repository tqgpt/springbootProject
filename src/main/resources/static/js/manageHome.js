document.addEventListener("DOMContentLoaded", () => {
    searchSchool(1);
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
        event.preventDefault();

        // 폼 필드 초기화
        document.getElementById('cityName').value = "전체";
        document.getElementById('streetAddr').innerHTML=`<option value="전체">구/군</option>`
        document.getElementById('searchOption').value = "전체"
        document.getElementById('searchValue').value = "";

        searchSchool(1);
    });
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




/*
 * =========================================================검색=========================================================
 *
 * **/

let ex_cityName = '전체';         // 시/도
let ex_streetAddr = '전체';      // 구/군
let ex_search_option = '전체';   // 검색 조건
let ex_search_value = '';    // 검색 입력 값

// 검색 버튼 클릭 시
document.getElementById('searchBtn').addEventListener('click', function (event) {
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
    document.querySelector('.page-link[data-page="1"]').closest('.page-item').classList.add('active');
});


//학교 데이터 출력
const searchSchool = (pageNumber) => {
    const tableBody = document.getElementById('tableBody');
    const searchParams = getParams(pageNumber);

    tableBody.innerHTML = '';
    fetch('/high/school/search-list', {
        method: 'POST', // POST 요청 사용
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(searchParams)
    }).then(response => response.json())
        .then(data => {

            data.forEach((school) => {
                const row = document.createElement('tr');
                row.onclick = () => {
                    location.href=`/high/school/info/${school.idx}`
                };

                const dateObj = new Date(school.createdAt);
                const formattedDate = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;

                row.innerHTML = `
                    <th>${school.idx}</th>
                    <td>${school.cityName}</td>
                    <td>${school.streetAddr}</td>
                    <td>${school.schoolKind}</td>
                    <td>${school.schoolName}</td>
                    <td>${school.cityEduOrg}</td>
                    <td>${school.localEduOrg}</td>
                    <td>${school.userName}</td>
                    <td>${formattedDate}</td>
                `;

                row.onclick = function() {
                    location.href = `/high/school/info/${school.idx}`;
                };
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
const getParams = (pageNumber) => {
    return {
        cityName: ex_cityName !== "" ? ex_cityName : null,
        streetAddr: ex_streetAddr !== "" ? ex_streetAddr : null,
        searchOption: ex_search_option !== "" ? ex_search_option : null,
        searchValue: ex_search_value !== "" ? ex_search_value : "",
        page: pageNumber ? pageNumber.toString() : '1'
    };
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

// 페이징 처리
const totalCount = 108; //임의의 count값
const pageCount = 5;
let currentPage = 1;

function generatePagination() {
    let totalPage = Math.ceil(totalCount / pageCount);
    let pageGroup = Math.ceil(currentPage / pageCount);

    let lastNumber = pageGroup * pageCount;
    if (lastNumber > totalPage) {
        lastNumber = totalPage;
    }
    let firstNumber = lastNumber - (pageCount - 1);
    currentPage = firstNumber;

    let paginationHTML = '';

    for (let i = firstNumber; i <= lastNumber; i++) {
        paginationHTML += `<li class="page-item ${i === currentPage ? 'active' : ''}"><label class="page-link" data-page="${i}" onclick="goPage(this)">${i}</label></li>`;
    }

    paginationHTML = `
    <li class="page-item"><a class="page-link" aria-label="Previous" onclick="goPrevious()"><label aria-hidden="true">이전</label></a></li>
    ${paginationHTML}
    <li class="page-item"><a class="page-link" aria-label="Next" onclick="goNext()"><label aria-hidden="true">다음</label></a></li>
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

generatePagination(); // 페이지네이션 생성

const goPrevious = () => {
    event.preventDefault();
    currentPage -= pageCount; // 이전 그룹으로 이동
    if (currentPage < 1) {
        currentPage = 1;
    }
    generatePagination();
    searchSchool(currentPage);
};

const goNext = () => {
    event.preventDefault();
    currentPage += pageCount; // 다음 그룹으로 이동
    if (currentPage > totalCount) {
        currentPage = totalPage;
    }
    generatePagination();
    searchSchool(currentPage);
};