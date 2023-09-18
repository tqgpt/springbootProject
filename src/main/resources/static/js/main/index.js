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
    if (response.ok) {
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
        // searchRelate(addressInput);
    } catch (error) {
        console.error("데이터 가져오기 오류:", error);
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

    if (response.ok) {
        const dataArray = await response.json();

        inputResult.innerHTML = "";

        dataArray.forEach((resultKeyword, index) => {
            createRelatedButton(resultKeyword, keyword, index);
            // createRelatedButton(resultKeyword, keyword)
        });

        inputResult.style.display = "block";
        // setupKeyboardNavigation();
    } else {
        inputResult.style.display = "none";
    }
};


// 디바운스 함수
function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        if (timeoutId) {
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    };
}

let debouncedSearchRelate;
debouncedSearchRelate = debounce(searchRelate, 5);


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

let focusedButtonIndex = -1;

//연관검색어
addressInput.addEventListener("keyup", (event) => {
    debouncedSearchRelate();
    // setupKeyboardNavigation(); // 추가
    if (event.key === "Enter") {
        handleSearch();
        clearSearchRelate();
    }
    // if (event.key === "ArrowDown") {
    //     const buttons = parentElement.querySelectorAll("button");
    //     if (buttons.length > 0) {
    //         // 현재 포커스된 요소가 버튼이면, 다음 버튼에 포커스를 설정
    //         const focusedButtonIndex = Array.from(buttons).indexOf(event.target);
    //         if (focusedButtonIndex !== -1 && focusedButtonIndex < buttons.length - 1) {
    //             buttons[focusedButtonIndex + 1].focus();
    //         }
    //     }
    // }
});
// 키보드 이벤트 리스너를 등록합니다.
// addressInput.addEventListener('keydown', function(event) {
//
//     // 엔터 키를 눌렀을 때 처리
//     if (event.key === "Enter") {
//         // 포커스된 버튼을 클릭합니다.
//         const focusedButton = document.activeElement;
//         if (focusedButton && focusedButton.tagName === "BUTTON") {
//             focusedButton.click();
//         }
//     }
//     if (event.key === 'ArrowDown') {
//         event.preventDefault();
//
//         const relatedButtons = document.querySelectorAll("#input_result button");
//
//         if(focusedButtonIndex === -1 && relatedButtons.length > 0){
//             focusedButtonIndex++;
//             relatedButtons[focusedButtonIndex].focus();
//         } else{
//             moveFocusDown();
//         }
//
//     }
// });

const fetchDataForRelatedKeyword = async (keyword) => {
    try {
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
    } catch (error) {
        console.error("데이터 가져오기 오류:", error);
    }
};


function createRelatedButton(resultKeyword, keyword) {
    const span = document.createElement("span");
    const button = document.createElement("button");

    span.className = "row";
    button.className = "btn text-start";
    button.tabIndex = 0;
    const coloredKeyword = resultKeyword.replace(new RegExp(keyword, "gi"), (match) => `<span class="text-success">${match}</span>`);

    button.innerHTML = coloredKeyword;
    span.appendChild(button);
    inputResult.appendChild(span);
    // setupKeyboardNavigation();
    button.dataset.keyword = resultKeyword;

    // button.addEventListener("focus", () => {
    //     focusedButtonIndex = button.dataset.index;
    // });
    // button.addEventListener("blur", () => {
    //     focusedButtonIndex = -1;
    // });
    // button.addEventListener("keydown", handleRelatedButtonKeyDown); // 키보드 이벤트 핸들러 등록
    // button.addEventListener("click", async (e) => {
    //     e.stopPropagation();
    //     e.preventDefault();
    //
    //     await fetchDataForRelatedKeyword(e.target.innerText);
    //     addressInput.value = buttonText;
    //     clearSearchRelate();
    // });
}

// function handleRelatedButtonKeyDown(event) {
//     if (event.key === "ArrowUp") {
//         event.preventDefault();
//         moveFocusUp();
//     } else if (event.key === "ArrowDown") {
//         event.preventDefault();
//         moveFocusDown();
//     } else if (event.key === 'Enter') {
//         this.click();
//     }
// }


// 이벤트 위임을 사용하여 부모 요소에 클릭 이벤트 리스너 추가
const parentElement = document.querySelector("#input_result");

// 부모 요소에 클릭 이벤트 리스너 추가
parentElement.addEventListener("click", (event) => {
    // 클릭된 요소 확인
    const targetElement = event.target;

    // 클릭된 요소가 버튼인지 확인
    if (targetElement.tagName === "BUTTON") {
        // 버튼 클릭 시 수행할 동작
        const buttonText = targetElement.innerText;
        fetchDataForRelatedKeyword(buttonText);
        addressInput.value = buttonText;
        clearSearchRelate();
    }
});


// 검색어 지우기
const clearSearchRelate = () => {
    const inputResult = document.querySelector("#input_result");
    inputResult.style.display = "none";
    inputResult.innerHTML = "";
};


// const relatedButtons = document.querySelectorAll("#input_result button");

// 키보드 이벤트 리스너를 등록합니다.
// addressInput.addEventListener("keydown", (event) => {
//     if (relatedButtons.length === 0) return; // 연관 검색어 버튼이 없으면 종료합니다.
//
//     switch (event.key) {
//         case "ArrowUp":
//             event.preventDefault();
//             moveFocusUp();
//             break;
//         case "ArrowDown":
//             event.preventDefault();
//             moveFocusDown();
//             break;
//         case "Enter":
//             if (focusedButtonIndex !== -1) {
//                 // 포커스된 버튼을 클릭합니다.
//                 relatedButtons[focusedButtonIndex].click();
//             }
//             break;
//     }
// });

// 포커스를 위로 이동하는 함수
// function moveFocusUp() {
//     const dynamicButtons = document.querySelectorAll("#input_result button");
//     const focusedButtonIndex = Array.from(dynamicButtons).findIndex((button) => document.activeElement === button);
//
//     if (focusedButtonIndex > 0) {
//         dynamicButtons[focusedButtonIndex].blur();
//         dynamicButtons[focusedButtonIndex - 1].focus();
//     }
// }
//
// // 포커스를 아래로 이동하는 함수
// function moveFocusDown() {
//     const relatedButtons = document.querySelectorAll("#input_result button");
//     if (focusedButtonIndex < relatedButtons.length - 1) {
//         relatedButtons[focusedButtonIndex].blur();
//         focusedButtonIndex++;
//         relatedButtons[focusedButtonIndex].focus();
//     }
// }

// addressInput에 대한 keydown 이벤트 리스너 추가
// addressInput.addEventListener("keydown", (event) => {
//     // 엔터 키를 눌렀을 때 처리
//     if (event.key === "Enter") {
//         // 포커스된 버튼을 클릭합니다.
//         const focusedButton = document.activeElement;
//         if (focusedButton && focusedButton.tagName === "BUTTON") {
//             focusedButton.click();
//         }
//     }
//     // 화살표 키를 눌렀을 때 처리
//     if (event.key === "ArrowUp" || event.key === "ArrowDown") {
//         event.preventDefault();
//         if (event.key === "ArrowUp") {
//             moveFocusUp();
//         } else {
//             moveFocusDown();
//         }
//     }
// });


// input_result 요소에 포커스가 갔을 때 이벤트 핸들러 등록
// parentElement.addEventListener("focusin", (event) => {
//     const buttons = parentElement.querySelectorAll("button");
//     if (buttons.length > 0) {
//         // 생성된 버튼이 있을 때, 첫 번째 버튼에 포커스를 설정
//         buttons[0].focus();
//     }
// });


// 연관 검색어 버튼에 포커스 설정
// const setFocusOnRelatedButtons = () => {
//     const relatedButtons = document.querySelectorAll("#input_result button");
//     if (relatedButtons.length > 0) {
//         relatedButtons[0].focus();
//     }
// };


// function setupKeyboardNavigation() {
//     const relatedButtons = document.querySelectorAll("#input_result button");
//
//     relatedButtons.forEach((button, index) => {
//         // 포커스 이벤트 리스너
//         button.addEventListener("focus", () => {
//             focusedButtonIndex = index;
//         });
//
//         // 블러 이벤트 리스너
//         button.addEventListener("blur", () => {
//             focusedButtonIndex = -1;
//         });
//
//         // 키보드 이벤트 리스너
//         button.addEventListener("keydown", (event) => {
//             if (event.key === "ArrowUp") {
//                 event.preventDefault();
//                 if (focusedButtonIndex > 0) {
//                     relatedButtons[focusedButtonIndex - 1].focus();
//                 }
//             } else if (event.key === "ArrowDown") {
//                 event.preventDefault();
//                 if (focusedButtonIndex < relatedButtons.length - 1) {
//                     relatedButtons[focusedButtonIndex + 1].focus();
//                 }
//             } else if (event.key === 'Enter') {
//                 button.click();
//             }
//         });
//     });
// }


// 검색어 입력창에 포커스가 갔을 때 이벤트 핸들러 등록
// addressInput.addEventListener("focusin", (event) => {
//     setFocusOnRelatedButtons();
// });

// 연관 검색어 버튼에 포커스 설정 및 이벤트 핸들러 등록
// const setFocusOnRelatedButtons = () => {
//     const relatedButtons = document.querySelectorAll("#input_result button");
//     if (relatedButtons.length > 0) {
//         relatedButtons[0].focus();
//
//         relatedButtons.forEach((button, index) => {
//
//             button.addEventListener("focus", () => {
//                 focusedButtonIndex = index;
//             });
//
//             button.addEventListener("blur", () => {
//                 focusedButtonIndex = -1;
//             });
//
//             button.addEventListener("keydown", (event) => {
//
//                 if (event.key === 'Enter') {
//                     button.click();
//                     event.preventDefault();
//                 }
//
//                 if (event.key === 'ArrowUp') {
//                     event.preventDefault();
//
//                     if (focusedButtonIndex > 0) {
//                         focusedButtonIndex--;
//                         relatedbuttons[focuededButttonindex].focus();
//                     } else {
//                         addressInput.focus();
//                     }
//                 } else if (event.keyCode === 40) {
//                     event.preventDefault();
//
//                     if (focuededButttonindex < relatedbuttons.length - 1) {
//                         focuededButttonindex++;
//                         realtedbuttons[focuededButttonindex].focus();
//
//                     } else {
//                         addressInput.focus();
//                     }
//                 }
//
//             });
//         });
//     }
// }