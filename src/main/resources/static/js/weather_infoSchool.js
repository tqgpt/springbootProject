//LCC(Lambert Conformal Conic)  는 위도와 경도를 그리드 좌표료 변환, 혹은 반대의 역할을 수행
//DFS(Datum Feature Shift)는 LCC의 변환작업 중 하나를 수행. 대충 곡면인 지구에서 좌표를 나타내는 역할. 현재는 자세히 공부할 생각은 없음
//이 js를 통해서 기상청에서 격자로 리턴값을 받아도 우리가 쓰는 위도 경도로 변환해서 맵에 있는 위도 경도와 일치하는 것의 실황을 보여줄 생각

// <!DOCTYPE html>
// <html lang="ko"
// xmlns:th="http://www.thymeleaf.org"
// xmlns="http://www.w3.org/1999/html">
//     <head>
//     <title>SchoolWeather | MapConverter</title>
// <meta charset="utf-8">
//     <meta name="viewport" content="width=device-width, initial-scale=1">
// <script language="javascript">
// html안에 넣을거라면
// ---------------------------------------------------------------------------------------------------------


const searchWeather = (address) => {
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

        // 지구 반경(km) - (Radius Earth)
        const RE = 6371.00877;

        // 격자 간격(km)
        const GRID = 5.0;

        // 투영 위도1(degree)
        const SLAT1 = 30.0;

        // 투영 위도2(degree)
        const SLAT2 = 60.0;

        // 경도(degree) - (LONGTITUDE) - X축
        const OLONG = 126.0;

        // 위도(degree) - (LATITUDE) - Y축
        const OLA = 38.0;

        // X좌표(GRID)
        const XO = 43;

        // Y좌표(GRID)
        const YO = 136;

// LCC DFS 좌표변환 ( code : "toXY"(위경도->좌표, v1:위도, v2:경도), "toLL"(좌표->위경도,v1:x, v2:y) )

        function convertXY(code, v1, v2) {
            var DEGRAD = Math.PI / 180.0;  //  각도를 원주율로 변환 = 원주율을 180으로 나눔
            var RADDEG = 180.0 / Math.PI;  //  원주율을 각도로 변환 = 180을 원주율로 나눔

            var re = RE / GRID;  //  RE(지구 반경)을 GRID(격자 간격)으로 나눔
            var slat1 = SLAT1 * DEGRAD;  //
            var slat2 = SLAT2 * DEGRAD;  //
            var olong = OLONG * DEGRAD;  //  X축 * 원주율
            var ola = OLA * DEGRAD;  //      Y축 * 원주율

            var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            var ro = Math.tan(Math.PI * 0.25 + ola * 0.5);
            ro = re * sf / Math.pow(ro, sn);
            var rs = {};

            // 용례
            // var rs = convertXY("toLL","60","127");
            // console.log(rs.la, rs.long);

            //위도 경도 -> 좌표로 변환
            if (code === "toXY") {
                //  결과값 위도 = v1  /  경도 = v2
                rs['latitude'] = v1;
                rs['longitude'] = v2;

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

            }
            else if (code === "toLL"){
                rs['x'] = v1;
                rs['y'] = v2;
                var xn = v1 - XO;
                var yn = ro - v2 + YO;
                ra = Math.sqrt(xn * xn + yn * yn);
                if (sn < 0.0) - ra;
                var ala = Math.pow((re * sf / ra), (1.0 / sn));
                ala = 2.0 * Math.atan(ala) - Math.PI * 0.5;

                if (Math.abs(xn) <= 0.0) {
                    theta = 0.0;
                }
                else {
                    if (Math.abs(yn) <= 0.0) {
                        theta = Math.PI * 0.5;
                        if (xn < 0.0) - theta;
                    }
                    else theta = Math.atan2(xn, yn);
                }
                var along = theta / sn + olong;
                rs['latitude'] = ala * RADDEG;
                rs['longitude'] = along * RADDEG;
            }

            var rs = convertXY("toXY",itemX,itemY);
            console.log(rs.x, rs.y);
        }

    });
}


// LCC DFS 좌표변환을 위한 기초 자료


// ---------------------------------------------------------------------------------------------------------
// html안에 넣을거라면
// </script>
// </head>
// <body>
// <textarea id="taLatLon" >37.579871128849334, 126.98935225645432
// 35.101148844565955, 129.02478725562108
// 33.500946412305076, 126.54663058817043</textarea>
// <div class="btnGroup">
//     <button onclick="fnLatLon2XY()">위경도 -> GridXY</button><br/><br/>
//     <button onclick="fnXY2LatLon()">위경도 &lt;- GridXY</button>
// </div>
//
// <textarea id="taXY"></textarea>
//
// </body>
// </html>




const test = () => {
    var xhr= new XMLHttpRequest(); // JSON 형태도 요청이 가능하구나.
    var url = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst'; /*URL*/
    var
        queryParams = '?' + encodeURIComponent('serviceKey') + '=' + 'bjzcjXp5BZXRA5vnoLptqnIRMijNPrHGZAwXuxDO1XkJ5j8V5zSpfRVB4OedKWbyiVdgfUGga8zoxwTnQnO00w'; /*Service Key*/
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /*페이지번호*/
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('1000'); /*한 페이지 결과 수*/
    queryParams += '&' + encodeURIComponent('dataType') + '=' + encodeURIComponent('JSON'); /*나는 제이슨으로 받을래*/
    queryParams += '&' + encodeURIComponent('base_date') + '=' + encodeURIComponent(String.valueOf(new Date())); /*23년 9월 13일 발표*/
    queryParams += '&' + encodeURIComponent('base_time') + '=' + encodeURIComponent(String.valueOf(new Date().getHours())); /*06시 발표(정시단위)*/
    queryParams += '&' + encodeURIComponent('nx') + '=' + encodeURIComponent('55'); /*X 좌표*/
    queryParams += '&' + encodeURIComponent('ny') + '=' + encodeURIComponent('127'); /*Y 좌표*/
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
// test()