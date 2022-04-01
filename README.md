# Paidy Forex

Prerequisites
* JDK (verified with JDK 17)
* Apache Maven

## Build and test the application
To build and run unit tests, run the following command in the root folder
```bash
mvn verify
```

To run the application, run the following commands
```
docker compose up -d  # This runs the paidyinc/one-frame container
./run.sh  # This runs application on port 80. To use a different port, config.properties needs to be modified.
```

To get an exchange rate, run the following command
```bash
curl "http://127.0.0.1/?from=USD&to=JPY"
```

An example of the output looks like
```bash
{"price":0.71,"from":"USD","to":"JPY"}
```

## API Specification
The following is the more formal API specification.

* Endpoint: http://127.0.0.1/
* HTTP Method: GET

<table>
  <tr>
   <th>
Parameter
   </th>
   <th>Type
   </th>
   <th>Required
   </th>
   <th>Description
   </th>
  </tr>
  <tr>
   <td>from
   </td>
   <td>string
   </td>
   <td>yes
   </td>
   <td>Source currency code. Supported currency codes are AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD.
   </td>
  </tr>
  <tr>
   <td>to
   </td>
   <td>string
   </td>
   <td>yes
   </td>
   <td>Target currency code. Supported currency codes are AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD.
    </td>
  </tr>
</table>

### Successful Response
* Response headers
  * Content-Type: application/json
* Response body
<table>
  <tr>
   <th>
Parameter
   </th>
   <th>Type
   </th>
   <th>Description
   </th>
  </tr>
  <tr>
   <td>from
   </td>
   <td>string
   </td>
   <td>Source currency code.
   </td>
  </tr>
  <tr>
   <td>to
   </td>
   <td>string
   </td>
   <td>Target currency code.
    </td>
  </tr>
  <tr>
   <td>price
   </td>
   <td>number
   </td>
   <td>Exchange rate of the given currency pair.
    </td>
  </tr>
</table>

Example of response
```
HTTP/1.1 200 OK
Date: Fri, 01 Apr 2022 13:33:20 GMT
Content-type: application/json
Content-length: 38

{"price":0.29,"from":"USD","to":"JPY"}
```
### Error Response
When either or both of from or to parameters are missing, the application returns 400 response. Here is an example response when to parameter is missing.

```
HTTP/1.1 400 Bad Request
Date: Fri, 01 Apr 2022 13:39:26 GMT
Content-type: application/json
Content-length: 73

{"message":"Parameters are invalid","errors":{"to":"Required parameter"}}
```

When one-frame API is unavailable (when the container is not running or reached the rate limit), the application returns 503 response. Here is the response example.

```
HTTP/1.1 503 Service Unavailable
Date: Fri, 01 Apr 2022 13:45:32 GMT
Content-type: application/json
Content-length: 48

{"message":"Service is temporarily unavailable"}
```
