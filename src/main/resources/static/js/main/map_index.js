// 현재 위치로 이동 이미지
var curtBtn = '<button class="btn btn-outline-success m-2 border-0"><i class="bi bi-compass fs-4"></i></button>';

// 현재 위치 위도, 경도 좌표 객체를 담을 변수
var curtLoca = "";

// Map 초기화
var map = new naver.maps.Map('map', {
    scaleControl: true,      // 우측 하단 scale 표시
    mapDataControl: false,    // 좌측 하단 @ NAVER Corp 표시
    zoom: 16,                  // 지도 줌 레벨
    zoomControl: true,
});


// Map 사용자 정의 컨트롤 이벤트 추가 (현재위치로 이동 버튼을 추가)
naver.maps.Event.once(map, 'init_stylemap', function() {
    /*
        현재 위치로 이동 img tag 변수를 CustomControl에 설정
        표시될 위치는 맵의 우측 상단
    */
    var cstmCtrl = new naver.maps.CustomControl(curtBtn, {
        position: naver.maps.Position.RIGHT_TOP
    });

    // CustomControl를 Map에 설정
    cstmCtrl.setMap(map);

    // 클릭 이벤트 리스너 설정
    naver.maps.Event.addDOMListener(cstmCtrl.getElement(), 'click', function() {
        if (curtLoca) {
            // 얻은 좌표를 지도의 중심으로 설정
            map.setCenter(curtLoca);
            // 지도의 줌 레벨을 변경
            map.setZoom(16);
        }
        else {
            alert("위치 액세스가 거부되었습니다.\n사용하시려면 위치 액세스를 허용해주세요.");
        }
    });
});

// getCurrentPosition 성공 콜백 함수
var onSuccessGeolocation = function (position) {
    // 현재위치
    curtLoca = new naver.maps.LatLng(position.coords.latitude, position.coords.longitude);

    // 얻은 좌표를 지도의 중심으로 설정합니다.
    map.setCenter(curtLoca);

    // 지도의 줌 레벨을 변경합니다.
    map.setZoom(16);

    // 현재 위치에 마커 표시
    new naver.maps.Marker({
        position: curtLoca,
        map: map,
    });
}


// getCurrentPosition 에러 콜백 함수
var onErrorGeolocation = function () {

    var agent = navigator.userAgent.toLowerCase(), name = navigator.appName;

    if (name === 'Microsoft Internet Explorer' || agent.indexOf('trident') > -1 || agent.indexOf('edge/') > -1) {
        alert("지원하지 않는 브라우져입니다.");
    }
    else {
        console.log("현재 위치를 가져오는데 에러가 발생하였습니다.");
    }
}

// Geolocation HTML5 API를 통해 얻은 현재 위치 좌표로 지도를 이동합니다.
if (navigator.geolocation) {
    /**
     * navigator.geolocation 은 Chrome 50 버젼 이후로 HTTP 환경에서 사용이 Deprecate 되어 HTTPS 환경에서만 사용 가능 합니다.
     * http://localhost 에서는 사용이 가능하며, 테스트 목적으로, Chrome 의 바로가기를 만들어서 아래와 같이 설정하면 접속은 가능합니다.
     * chrome.exe --unsafely-treat-insecure-origin-as-secure="http://example.com"
     */
    navigator.geolocation.getCurrentPosition(onSuccessGeolocation, onErrorGeolocation);
}
else {
    console.log("Geolocation Not supported Required");
}

// 주소 => 위/경도 조회
const searchAddressToCoordinate = (address) => {
    return new Promise((resolve, reject) => {
        naver.maps.Service.geocode({ query: address },
            (status, response) => {
                if (status === naver.maps.Service.Status.ERROR) {
                    if (!address) {
                        reject('Geocode Error, Please check address');
                    } else {
                        reject('Geocode Error, address:' + address);
                    }
                } else if (response.v2.meta.totalCount === 0) {
                    reject('Geocode No result.');
                } else {
                    const item = response.v2.addresses[0];
                    const lat = item.y;
                    const lng = item.x;

                    // 이동할 위치를 위도(lat)와 경도(lng)로 설정
                    const newPosition = new naver.maps.LatLng(lat, lng);

                    map.setCenter(newPosition); // 이동할 위치로 지도 이동
                    map.setZoom(17);            // 원하는 줌 레벨로 설정

                    new naver.maps.Marker({
                        position: new naver.maps.LatLng(lat, lng),
                        map: map,
                    });

                    resolve(item);
                }
            });
    });
}