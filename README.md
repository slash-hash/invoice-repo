# invoice-repo

Comments

allowed urls
/invoices list of all existing invoices
/update/{id} with json body and existing id in invoices list
/add with body
/delete/{id}
/invoice/bysalesid/{id} searches invicoices by other system id

Along with already used fields. It would be wise to add on some additional fields:

- Reference to other systems like Sales systems Id. This might need some restrictions like that values is unique
- Date(s) - there can be actually couple like invoice creation date, due date when invoice should be paid, date when invoice was uploaded etc.
- boolean - to confirm that invoice has beed paid

Suggested further steps and optimization: For now as invoice backend only stores data and does not do much of business logic therefore controller and repository are used. But for future extension we can add service (@Service) for validation and other added logic. 
Splitting into multiple entities 


For logic, depending no how much other systems are already doing. For example we can add on customer validation. Validate if the customer is really existing. Validate if product is existing.

* adding invoice
curl -X POST http://127.0.0.1:8080/add/ -d "{\"customerInternalIdentifier\" : \"GoodCustomer\", \"customerName\" : \"JetBrains\", \"customerAddress\" : \"Prague\" , \"orderDate\" : \"2020-12-10\", \"isoCurrency\":\"USD\", \"orderReferenceNumber\":6021, \"productSKU\":\"fsdgff\", \"productName\":\"new laptop\", \"productQuantity\":15, \"price\":256.20}" -H "Content-Type: application/json"
