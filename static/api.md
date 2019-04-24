# API Documentation

## Static Files
Any file within the static directory will be served as though 'static' was the root of the webserver. For example `index.html` can be found at `/index.html`.

## Usage data
### Endpoint: `/data`

### Method: `PUT`
`PUT` can be used to add new records to the usage database.<br>
Example:
```javascript
$.ajax({
    url: "http://localhost:8000/data",
    type: "PUT",
    // hostname, seconds spent on site, interaction start time (unix timestamp)
    data: {host: "example.com", seconds: 42, start: 1555297610},
    success: function(response) { console.log(response) }
})
```

## Usage statistics
### Endpoint: `/stats`

### Method `GET`
`GET` can be used to determine the number of seconds that the user has spent on both distracting and focused sites during the current calendar day.<br>
Example:
```javascript
$.ajax({
    url: "http://localhost:8000/stats",
    type: "GET",
    success: function(response) { console.log(response) }
})
```