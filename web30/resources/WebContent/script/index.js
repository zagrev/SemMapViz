/**
 * routines for the index page. Specifically, this will load the time range and add the callbacks for map clicks
 */

var map;
var dateFormat = "dd MMM yyyy";
var bbox = {ul:"", ur:"", ll:"", lr:""};

$(document).ready(function()
{
   createMap();

   $.getJSON("tweet/timerange", function(r)
   {
      var min = new Date(r.minTime);
      var max = new Date(r.maxTime);

      $("#minTime").html($.format.date(min, dateFormat));
      $("#maxTime").html($.format.date(max, dateFormat));

      createSlider(min.getTime(), max.getTime(), min.getTime());
   })
});

function createMap()
{
   map = new ol.Map(
   {
      target : 'map',
      layers : [ new ol.layer.Tile(
      {
         source : new ol.source.MapQuest(
         {
            layer : 'sat'
         }),
      }),
// new ol.layer.Tile({
// source: new ol.source.TileJSON({
// / url: 'http://api.tiles.mapbox.com/v3/mapbox.natural-earth-hypso-bathy.jsonp',
// crossOrigin: 'anonymous',
// maxResolution: 20
      // }),
      // })
// new ol.layer.Tile({
// source: new ol.source.TileJSON({
// url: 'http://api.tiles.mapbox.com/v3/' +
// 'mapbox.world-borders-light.jsonp',
// crossOrigin: 'anonymous'
// })
      ],
      view : new ol.View(
      {
         center : ol.proj.transform([ -84.0648, 39.7798 ], 'EPSG:4326', 'EPSG:3857'),
         zoom : 14
      }),
      controls : [ new ol.control.Zoom(), new ol.control.ZoomSlider(
      {
         maxResolution : 20,
         minResolution : 100
      }), new ol.control.ScaleLine(),
      // mapTimeSlider
      ]
   });
}

function createSlider(min, max, value)
{
// Create a YUI sandbox on your page.
   YUI().use('node', 'event', 'slider', function(Y)
   {
      var slider = new Y.Slider(
      {
         axis : 'x',
         min : min,
         max : max,
         value : value,
      });
      
      slider.render("#time-slider");

      slider.after("thumbMove", function()
      {
         // update date display so the user can tell where they are selecting
         var date = new Date(slider.getValue());
         $("#currentDate").html($.format.date(date, dateFormat))
      });
      
      slider.after("slideEnd", function()
      {
         var date = new Date(slider.getValue());
         getData(date);
      });

   });
   
   function getData(newDate)
   {
      
   }
   
   function getData(bbox, newDate)
   {
      
   }
}
