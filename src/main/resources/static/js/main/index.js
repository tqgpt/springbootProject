const addressInput = document.getElementById("addressInput");
const searchButton = document.getElementById("button-addon");
const searchResultDiv = document.getElementById("search_result");

document.addEventListener("DOMContentLoaded",() => {
    showCookies();
})

const getCookie = (key) => {
    const cookies = document.cookie.split(`; `).map((el) => el.split('='));
    let getItem = null;

    for (let i = 0; i < cookies.length; i++) {
        if (cookies[i][0] === key) {
            getItem = cookies[i][1];
            break;
        }
    }

    if (getItem) {
        try {
            const decodedValue = decodeURIComponent(getItem);
            return JSON.parse(decodedValue);
        } catch (error) {
            console.error("JSON 파싱 오류:", error);
            return null;
        }
    }

    return null;
};

const recordCookie = (school) => {
    const MAX_DATA_COUNT = 5; // 최대 저장할 데이터 개수
    const cookieName = 'visit_record';
    let existingData = getCookie(cookieName); // 이전 데이터 가져오기
    const newData = school;

    if (!existingData) {
        existingData = {}; // 이전 데이터가 없으면 빈 객체로 초기화
    }

    const existingKeys = Object.keys(existingData);
    let newKey;

    // 데이터 개수가 최대 개수를 초과하는 경우, 가장 오래된 데이터를 제거하고 새 키를 생성합니다.
    if (existingKeys.length >= MAX_DATA_COUNT) {
        const oldestKey = Math.min(...existingKeys.map(Number)); // 가장 오래된 데이터 키 찾기
        delete existingData[oldestKey]; // 가장 오래된 데이터 제거
        newKey = Math.max(...existingKeys.map(Number)) + 1; // 새 데이터 키 생성
    } else {
        newKey = existingKeys.length + 1; // 새 데이터 키 생성
    }

    existingData[newKey] = newData; // 새 데이터를 기존 데이터에 추가

    const schoolDataJSON = JSON.stringify(existingData); // 기록할 데이터 JSON으로 변환
    const expirationDate = new Date();
    expirationDate.setDate(expirationDate.getDate() + 1); // 쿠키 만료일 1일 후로 설정

    document.cookie = `${cookieName}=${encodeURIComponent(schoolDataJSON)}; expires=${expirationDate.toUTCString()}; path=/`;
    showCookies();
};

const showCookies = () => {
    const outputElement = document.querySelector('#cookies');
    const jsonData = getCookie("visit_record");

    let htmlString = '<ul>';
    for (const key in jsonData) {
        if (jsonData.hasOwnProperty(key)) {
            const data = jsonData[key];
            htmlString += `<li>${data.schoolName} - ${data.streetAddr}</li>`;
        }
    }
    htmlString += '</ul>';

    outputElement.innerHTML = htmlString;
}



const findSchoolInfo = async (keyword) => {
    const response = await fetch(`/high/school/search/${keyword}`, {
        method: "GET",
        headers: {"Content-Type": "application/json"},
    });
    if (response.ok) {
        const dataArray = await response.json();
        initSchools(dataArray);
    } else {
        console.error("서버 응답 오류:", response.status, response.statusText);
    }
};


const initSchools = (dataArray) => {
    clearMarker();
    searchResultDiv.innerHTML = '';

    const htmlElements = dataArray.map((data) => {
        const schoolItem = document.createElement('div');
        schoolItem.classList.add('list-group-item', 'list-group-item-action', 'flex-column', 'align-items-start', 'my-1', 'rounded', 'bg-secondary-subtle');
        schoolItem.setAttribute('id', 'searchCard');
        schoolItem.onclick = () => {
            searchAddressToCoordinateMarker(data.streetAddr, 18);
            recordCookie(data);
        };
        searchAddressToCoordinateMarker(data.streetAddr, 12);
        schoolItem.innerHTML = `
          <div class="d-flex w-100 justify-content-between">
            <h5 class="mb-1">${data.schoolName}</h5>
            <small>${data.schoolKind}</small>
          </div>
          <p class="mb-1">${data.streetAddr}</p>
          <small>${data.localEduOrg}</small>
        `;

        return schoolItem;
    });

    searchResultDiv.append(...htmlElements);
}

const handleSearch = async () => {
    const keyword = addressInput.value;
    if (!keyword) { return }
    await findSchoolInfo(keyword);
};

searchButton.addEventListener("click", handleSearch); // 버튼 클릭 이벤트 처리
addressInput.addEventListener("keyup", (event) => {   // Enter 키 이벤트 처리
    if (event.key === "Enter") handleSearch();
});


//초중고 선택
// 초기 아이콘 인덱스 및 라벨 배열 정의
let currentIndex = 0; // 현재 아이콘의 인덱스
const icons = ["bi-layers-fill", "bi-layers-half", "bi-layers"];
const labels = ["high", "middle", "elementary"];

// 토글 버튼 클릭 이벤트 처리
toggleButton.addEventListener("click", () => {
    currentIndex = (currentIndex + 1) % icons.length; // 다음 아이콘 인덱스 계산
    icon.className = `bi ${icons[currentIndex]}`; // 아이콘 클래스 변경

    // 토글 버튼을 클릭할 때 라벨도 함께 전환
    const labelId = labels[currentIndex];
    toggleLabel(labelId);
});

// 라벨 토글 함수 정의
const toggleLabel = (labelId) => {
    labels.forEach((label) => {
        const element = document.getElementById(label);
        element.style.display = label === labelId ? "flex" : "none";
    });
}

// 각 아이콘에 대한 클릭 이벤트 처리
labels.forEach((label) => {
    const iconElement = document.getElementById(`${label}Icon`);
    iconElement.addEventListener("click", () => toggleLabel(label));
});



//연관검색어
// const showRelate = (input) => {
//     const keyword = input.value;
//     const response = fetch(`/high/school/search/${keyword}`, {
//         method: "GET",
//         headers: {"Content-Type": "application/json"},
//     });
//     if (response.ok) {
//         const dataArray = response.json();
//         console.log(dataArray)
//
//     } else {
//         console.error("서버 응답 오류:", response.status, response.statusText);
//     }
// }