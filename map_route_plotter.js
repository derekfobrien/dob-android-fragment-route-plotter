// transform arguments
var ta1 = "EPSG:4326";
var ta2 = "EPSG:900913";
var ta3 = "EPSG:3857";
var theFeatures = [];

var points = [];
var theStyles = [];
var request = new XMLHttpRequest();
var speedBands = new Array(160, 145, 130, 115, 100, 85, 70, 55, 40, 0);
var colourBands = new Array(
  "#ff0000",
  "#ff8800",
  "#ffff00",
  "#00ff00",
  "#008888",
  "#00ffff",
  "#0088ff",
  "#0000ff",
  "#8800ff",
  "#000000"
);
request.open("GET", "DublinToCork.json", true);
request.onload = function() {
  points = JSON.parse(this.response);
  output = "";

  var myLng = -9.045;
  var myLat = 53.272;
  var p1, p2;
  var myLine;

  // populate the styles
  for (var i = 0; i < speedBands.length; i++) {
    theStyle = new ol.style.Style({
      stroke: new ol.style.Stroke({
        color: colourBands[i],
        width: 5
      })
    });

    theStyles.push(theStyle);
  }

  var anotherStyle = new ol.style.Style({
    stroke: new ol.style.Stroke({
      color: "#ff3300",
      width: 3
    })
  });

  // try to draw in points from Json
  for (var i = 0; i < points.length - 1; i++) {
    myLng = parseFloat(points[i].longitude);
    myLat = parseFloat(points[i].latitude);
    p1 = [myLng, myLat];

    myLng = parseFloat(points[i + 1].longitude);
    myLat = parseFloat(points[i + 1].latitude);
    p2 = [myLng, myLat];

    myLine = new ol.geom.LineString([p1, p2]);

    myLine.transform(ta1, ta3);

    // the feature
    var theFeature = new ol.Feature(myLine);
    //set style by speed
    var speed = parseFloat(points[i].speed);

    var j = 0;
    while (speed < speedBands[j] && j < speedBands.length) {
      j++;
    }

    theFeature.setStyle(theStyles[j]);
    theFeatures.push(theFeature);
  } // end of the for-loop

  // source and vector layer
  var vecSource = new ol.source.Vector({
    features: theFeatures
  });

  var vecLayer = new ol.layer.Vector({
    source: vecSource,
    style: theStyle
  });

  // Tile layer
  var osmLayer = new ol.layer.Tile({
    source: new ol.source.OSM()
  });

  //Latitude and longitude
  var mapCentre = ol.proj.transform([-9.04, 53.27], ta1, ta2);

  // View
  var myView = new ol.View({
    center: mapCentre,
    zoom: 8
  });

  // Map
  var map = new ol.Map({
    target: "map",
    layers: [osmLayer, vecLayer],
    view: myView
  });

  // Draw in the Key
  var theKey = document.getElementById("key");
  var myHTML = "<table>";
  myHTML += '<tr><td class="col1">Colour</td><td class="col2">Speed</td></tr>';
  for (i = 0; i < speedBands.length; i++) {
    if (i == 0) {
      myHTML +=
        '<tr><td class="col1" style="background: ' +
        colourBands[i] +
        '"></td><td class="col2">' +
        speedBands[i] +
        "km/h and Over</td></tr>";
    } else {
      myHTML +=
        '<tr><td class="col1" style="background: ' +
        colourBands[i] +
        '"></td><td class="col2">' +
        speedBands[i] +
        " to " +
        speedBands[i - 1] +
        "km/h</td></tr>";
    }
  }
  myHTML += "</table>";
  theKey.innerHTML += myHTML;
}; // end of the onload function

request.send();
