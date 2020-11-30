# invoice-repo

* adding invoice
curl -X POST http://127.0.0.1:8080/add/ -d "{\"customerInternalIdentifier\" : \"GoodCustomer\", \"customerName\" : \"JetBrains\", \"customerAddress\" : \"Prague\" , \"orderDate\" : \"2020-12-10\", \"isoCurrency\":\"USD\", \"orderReferenceNumber\":6021, \"productSKU\":\"fsdgff\", \"productName\":\"new laptop\", \"productQuantity\":15, \"price\":256.20}" -H "Content-Type: application/json"
