const addressInput = document.getElementById("addressInput");
const searchButton = document.getElementById("button-addon");
const searchResultDiv = document.getElementById("search_result");
const inputResult = document.getElementById("input_result");

let isFetching = false;
document.addEventListener("DOMContentLoaded", () => {
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
        return JSON.parse(decodeURIComponent(getItem));
    }

    return null;
};

const recordCookie = (school) => {
    const MAX_DATA_COUNT = 5; // 최대 저장할 데이터 개수 (5가 최대)
    const cookieName = 'visitRecord';
    let existingData = getCookie(cookieName); // 이전 데이터 가져오기
    const newData = school;

    if (!existingData) {
        existingData = {};
    }

    const existingKeys = Object.keys(existingData);

    // 기존 데이터 중에 동일한 "idx"가 있는 경우 해당 데이터 삭제
    const existingIdx = existingKeys.find(key => existingData[key].idx === newData.idx);
    if (existingIdx) {
        delete existingData[existingIdx];
    }

    // 데이터 개수가 5를 초과하는 경우, 가장 오래된 데이터를 제거
    if (existingKeys.length >= MAX_DATA_COUNT) {
        const oldestKey = Math.min(...existingKeys.map(Number)); // 가장 오래된 데이터 키 찾기
        delete existingData[oldestKey]; // 가장 오래된 데이터 제거
    }

    // 새 데이터를 최신 데이터로 추가
    const newKey = new Date().getTime(); // 현재 시간을 키로 사용하여 최신 데이터로 추가
    existingData[newKey] = newData; // 새 데이터를 기존 데이터에 추가

    const schoolDataJSON = JSON.stringify(existingData); // 기록할 데이터 JSON으로 변환
    const expirationDate = new Date();
    expirationDate.setDate(expirationDate.getDate() + 7); // 쿠키 만료일 7일 후로 설정

    document.cookie = `${cookieName}=${encodeURIComponent(schoolDataJSON)}; expires=${expirationDate.toUTCString()}; path=/`;
    showCookies();
};


const showCookies = () => {
    const outputElement = document.querySelector('#cookies');
    const jsonData = getCookie("visitRecord");

    let htmlString = '<ul id="recentList" class="container-fluid">';
    for (const key in jsonData) {
        if (jsonData.hasOwnProperty(key)) {
            const data = jsonData[key];
            htmlString += `
                <li class="col-2 recent" onclick="searchAddressToCoordinateMarker('${data.streetAddr}', 18)">
                    <span>${data.schoolName}</span>
                    <p class="text-truncate">${data.streetAddr}</p>
                </li>`;
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
    if (response.status === 200) {
        const dataArray = await response.json();
        initSchools(dataArray);
    } else {
        console.log("일치하는 학교 없음", response.status, response.statusText);
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
    if (!keyword || isFetching) {
        return;
    }
    isFetching = true;
    searchButton.disabled = true;
    clearMarker();
    try {
        await findSchoolInfo(keyword);
        clearSearchRelate();
    } catch (error) {
        console.log("학교를 불러올수 없음 ", error.status, error.statusText);
    } finally {
        isFetching = false;
        searchButton.disabled = false;
    }
};


const searchRelate = async () => {
    inputResult.style.display = "none";
    const keyword = addressInput.value;

    if (keyword === "") {
        inputResult.style.display = "none";
        return false;
    }

    const response = await fetch(`/high/school/search-relate?keyword=${keyword}`, {
        method: "GET",
        headers: {"Content-Type": "application/json"},
    });

    if (response.status === 200) {
        const dataArray = await response.json();

        inputResult.innerHTML = "";

        dataArray.forEach((resultKeyword) => {
            createRelatedButton(resultKeyword, keyword);
        });
        inputResult.style.display = "block";
    } else {
        inputResult.style.display = "none";
    }
};

let debouncedSearchRelateTimeoutId;

// 디바운스 함수
function debounce(func, delay) {
    return function (...args) {
        if (debouncedSearchRelateTimeoutId) {
            clearTimeout(debouncedSearchRelateTimeoutId);
        }

        debouncedSearchRelateTimeoutId = setTimeout(() => {
            func.apply(this, args);
            debouncedSearchRelateTimeoutId = null;
        }, delay);
    };
}

let debouncedSearchRelate = debounce(searchRelate, 100);


searchButton.addEventListener("click", handleSearch);


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

const fetchDataForRelatedKeyword = async (keyword) => {
    try {
        const response = await fetch(`/high/school/search/${keyword}`, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        });

        if (response.status === 200) {
            const dataArray = await response.json();
            initSchools(dataArray);
        } else {
            console.log("학교를 불러올수 없음 ", response.status, response.statusText);
        }
    } catch (error) {
        console.log("일치하는 학교 없음", error.status, error.statusText);
    }
};


function createRelatedButton(resultKeyword, keyword) {
    const span = document.createElement("span");
    const button = document.createElement("button");

    span.className = "row";
    button.className = "btn text-start";
    const coloredKeyword = resultKeyword.replace(new RegExp(keyword, "gi"), (match) => `<span class="text-success">${match}</span>`);

    button.innerHTML = coloredKeyword;
    span.appendChild(button);
    inputResult.appendChild(span);
    button.dataset.keyword = resultKeyword;

}


const parentElement = document.querySelector("#input_result");

parentElement.addEventListener("click", (event) => {

    const targetElement = event.target;
    if (targetElement.tagName === "BUTTON") {
        const buttonText = targetElement.innerText;
        fetchDataForRelatedKeyword(buttonText);
        addressInput.value = buttonText;
        clearSearchRelate();
    }
});


const clearSearchRelate = () => {
    if (inputResult.style.display === "block") {
        inputResult.style.display = "none";
        inputResult.innerHTML = "";
    }
};


addressInput.addEventListener("focus", () => {
    searchRelate();
});
document.addEventListener("mousedown", (event) => {
    const targetElement = event.target;
    if (targetElement !== addressInput && !parentElement.contains(targetElement)) {
        clearSearchRelate();
    }
});
addressInput.addEventListener("keydown", async (event) => {
    if (event.key === "Enter") {
        event.preventDefault();
        if (debouncedSearchRelateTimeoutId) {
            clearTimeout(debouncedSearchRelateTimeoutId);
            debouncedSearchRelateTimeoutId = null;
        }

        await handleSearch();
    } else {
        debouncedSearchRelate();
    }
});



