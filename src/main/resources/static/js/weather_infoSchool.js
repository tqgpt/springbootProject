/*지도 위도, 경도 받아오기 START*/
const searchWeatherLocate = (address) => {
    naver.maps.Service.geocode({
        query: address
    }, function(status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
            return console.error('Something Wrong!');
        }
        if (response.v2.meta.totalCount === 0) {
            return console.error('totalCount' + response.v2.meta.totalCount);
        }
        const itemX = response.v2.addresses[0].x;
        const itemY = response.v2.addresses[0].y;
        /*지도 위도, 경도 받아오기 END*/

        // 지구 반경(km) - (Radius Earth)
        const RE = 6371.00877;
        // 격자 간격(km)
        const GRID = 5.0;
        // 투영 위도1(degree)
        const SLAT1 = 30.0;
        // 투영 위도2(degree)
        const SLAT2 = 60.0;
        // 경도(degree) - (LONGITUDE) - X축
        const OLONG = 126.0;
        // 위도(degree) - (LATITUDE) - Y축
        const OLAT = 38.0;
        // X좌표(GRID)
        const XO = 43;
        // Y좌표(GRID)
        const YO = 136;


        /*받은 위도, 경도를 <-> 그리드 좌표 X,Y 변환하기 START*/
        // 좌표변환 (code : "toXY"(위도, 경도 -> 좌표 v1:위도, v2:경도)  /  code : "toLL"(좌표 -> 위도, 경도 v1:x, v2:y) )
        var DEGRAD = Math.PI / 180.0;  //  각도를 원주율로 변환 = 원주율을 180으로 나눔

        var re = RE / GRID;  //  RE(지구 반경)을 GRID(격자 간격)으로 나눔
        var slat1 = SLAT1 * DEGRAD;  //
        var slat2 = SLAT2 * DEGRAD;  //
        var olong = OLONG * DEGRAD;  //  X축 * 원주율
        var olat = OLAT * DEGRAD;  //      Y축 * 원주율

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        var rs = {};



        //위도 경도 -> 좌표로 변환
        //  결과값 위도 = v1  /  경도 = v2
        const v1 = rs[itemX];
        const v2 = rs[itemY];

        //  복잡한 계산식
        var ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        var theta = v2 * DEGRAD - olong;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        //  결과값 x  /  y
        rs['x'] = Math.floor(ra * Math.sin(theta) + XO + 0.5);
        rs['y'] = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        fetchWeatherData(v1, v2);

    });
}


function fetchWeatherData(v1, v2) {
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = String(currentDate.getMonth() + 1).padStart(2, '0');
    const day = String(currentDate.getDate()).padStart(2, '0');
    const formattedDate = `${year}${month}${day}`;

    currentDate.setHours(currentDate.getHours() - 1); // 한 시간 전으로 설정
    const hours = String(currentDate.getHours()).padStart(2, '0');
    const minutes = String(currentDate.getMinutes()).padStart(2, '0');
    const formattedTime = `${hours}${minutes}`;

    var xhr= new XMLHttpRequest(); // JSON 형태도 요청이 가능하구나.
    var url = 'https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst'; /*URL*/
    var queryParams = '?' + encodeURIComponent('serviceKey') + '=' + 'bjzcjXp5BZXRA5vnoLptqnIRMijNPrHGZAwXuxDO1XkJ5j8V5zSpfRVB4OedKWbyiVdgfUGga8zoxwTnQnO00w'; /*Service Key*/
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /*페이지번호*/
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('1000'); /*한 페이지 결과 수*/
    queryParams += '&' + encodeURIComponent('dataType') + '=' + encodeURIComponent('JSON'); /*나는 제이슨으로 받을래*/
    queryParams += '&' + encodeURIComponent('base_date') + '=' + encodeURIComponent(formattedDate); /*날짜*/
    queryParams += '&' + encodeURIComponent('base_time') + '=' + encodeURIComponent(formattedTime); /*시간*/
    queryParams += '&' + encodeURIComponent('nx') + '=' + encodeURIComponent(v1); /*X 좌표*/
    queryParams += '&' + encodeURIComponent('ny') + '=' + encodeURIComponent(v2); /*Y 좌표*/
    xhr.open('GET', url + queryParams);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var jsonResponse = JSON.parse(this.responseText); // JSON 데이터 파싱
            console.log(jsonResponse);
            alert('Status: '+this.status+'nHeaders: '+JSON.stringify(this.getAllResponseHeaders())+'nBody: '+this.responseText);
        }
    };
    xhr.send('');
}