var map = new naver.maps.Map("map", {
    zoom: 15,
    mapTypeControl: true,
});

var infoWindow = new naver.maps.InfoWindow({
    anchorSkew: true
});

map.setCursor('pointer');

function searchAddressToCoordinate(address, schoolName) {
    naver.maps.Service.geocode({
        query: address
    }, function(status, response) {
        if (status === naver.maps.Service.Status.ERROR) {
            return console.log('Something Wrong!');
        }

        if (response.v2.meta.totalCount === 0) {
            return console.log('totalCount' + response.v2.meta.totalCount);
        }

        var htmlAddresses = [],
            item = response.v2.addresses[0],
            point = new naver.maps.Point(item.x, item.y);

        if (item.roadAddress) {
            htmlAddresses.push('[도로명 주소] ' + item.roadAddress);
        }

        if (item.jibunAddress) {
            htmlAddresses.push('[지번 주소] ' + item.jibunAddress);
        }

        if (item.englishAddress) {
            htmlAddresses.push('[영문명 주소] ' + item.englishAddress);
        }

        infoWindow.setContent([
            '<div style="padding:10px;width: auto;line-height:150%; ">',
            '<h4 style="margin-top:5px;">'+ schoolName +'</h4><br /><div id="mapContent">',
            htmlAddresses.join('<br />'),
            '</div></div>'
        ].join('\n'));

        map.setCenter(point);

        map.setOptions({
            zoomControl: true,
        });

        infoWindow.open(map, point);
    });
}

