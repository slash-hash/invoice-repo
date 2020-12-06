# invoice-repo

Invoice storage is based on postgres database with connection set up inside application.properties.
In case system grows larger splitting into multiple entities might make sense, currently system stores one main entity "Invoice". 

Along with fields sales system can provide, it would be wise to add on some additional fields:

1. salesSystemsId (long) - reference to other system, currently created as unique as I believe there should not be multiple invoices with same id
2. invoiceCreationDate (Date, one or more) - there can be actually couple like invoice creation date, due date when invoice should be paid, date when invoice was uploaded etc.
3. isPaid (boolean) - to confirm that invoice has been paid

Fields that are mandatory currently:
customerInternalIdentifier, productQuantity, price, salesSystemId(also has to be unique as we don't expect two invoices with same id)

Furthermore other fields can be considered like: type of invoice, is invoice recurring, person responsible for sales, reference to other system etc.

To start the app:from command line in unix systems just run ./gradlew bootRun after starting app it is running on default 8080 port and these endpoints are allowed:
/invoices - list of all existing invoices
/update/{id} - put message with json body and existing id in invoices list
/add - post message with json body
/delete/{id} - delete message with id of existing invoice (id is internal system id not sales system id)
/invoice/bysalesid/{id} - searches invoices by sales system id, the other systems will probably want to serach using this id

Suggested further steps and optimization: For now as invoice backend stores data and does not do much of business logic therefore spring controller and repository are used. For now adding for instance service feels like overkill, but for future extension we can add spring service for validation and other added logic.


From business perspective we can consider these features: 
1. Validate if the customer is really existing (what would be an invoice without a customer to charge for it :) ). 
2. Validate if product is existing.
3. Additional data output - total value of all invoices per customer, total of paid and unpaid invoices etc.
4. Reading of specific data - searching by internal id, list invoices by customer, by date or some other field. 

* adding invoice example
curl -X POST http://127.0.0.1:8080/add/ -d "{\"customerInternalIdentifier\" : 1, \"customerName\" : \"JetBrains\", \"customerAddress\" : \"Prague\" , \"orderDate\" : \"2020-12-10\", \"isoCurrency\":\"USD\", \"orderReferenceNumber\":6021, \"productSKU\":340955, \"productName\":\"new laptop\", \"productQuantity\":15, \"price\":256.20, \"invoiceCreationDate\": \"2014-05-30\", \"salesSystemId\":22}" -H "Content-Type: application/json"

* updating invoice
curl -X PUT http://127.0.0.1:8080/update/2 -d "{\"customerInternalIdentifier\":653,\"customerName\":\"JetBrains\",\"customerAddress\":\"Prague\",\"orderDate\":\"2020-12-10\",\"isoCurrency\":\"USD\",\"orderReferenceNumber\":6021,\"productSKU\":3928,\"productName\":\"new laptop\",\"productQuantity\":15,\"price\":256.2, \"salesSystemId\":203, \"invoiceCreationDate\": \"2014-05-30\", \"id\":2}," -H "Content-Type: application/json"


list of json fields with proper type:
long - customerInternalIdentifier, int - productQuantity, float - price, long - salesSystemId, 
String - customerName, String customerAddress, Date - orderDate, ISO currency code - isoCurrency, 
long - orderReferenceNumber, long - productSKU, String - productName