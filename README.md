# 쇼핑몰


# 개발 환경
* IntelliJ
* HeidiSql
* GitHub
* Swagger2

# 사용된 기술
## 백엔드
* Java 11
* Spring Boot 2.6.7
* Spring Boot JPA
* Spring Boot Security
* Spring Data Actuator
* MariaDB
* Redis
* Thymeleaf 

## 프론트엔드
* Vanilla JS
* Bootstrap 5.2

## DevOps
* Docker
* Jenkins
* Prometheus
* Grafana

# 프로젝트 회고
https://middleearth.tistory.com/75

# 프로젝트 구조
<details>
<summary>접기/펼치기</summary>

```
src
└───shoppingmall
    ├───common
    │   └───model
    ├───config
    │   └───interceptors
    ├───controller
    │   ├───api
    │   ├───common
    │   └───templates
    ├───demo
    ├───domain
    │   ├───browse_history
    │   ├───cart
    │   ├───category
    │   ├───image
    │   ├───item
    │   │   ├───item_option
    │   │   ├───item_price
    │   │   │   ├───model
    │   │   │   └───price_history
    │   │   ├───item_stock
    │   │   │   └───item_stock_history
    │   │   ├───item_view_history
    │   │   └───temporary
    │   ├───model
    │   │   └───page
    │   ├───notification
    │   │   ├───item_notification
    │   │   ├───me_notification
    │   │   ├───model
    │   │   └───qna_notification
    │   ├───order
    │   │   └───order_item
    │   │       └───order_delivery
    │   ├───payment
    │   │   ├───model
    │   │   └───payment_per_seller
    │   ├───point
    │   │   └───point_history
    │   │       └───model
    │   ├───qna
    │   ├───review
    │   ├───seller
    │   │   └───seller_bank_account_history
    │   ├───statistics
    │   ├───token
    │   │   └───password
    │   ├───user
    │   │   └───model
    │   ├───virtual_delivery_company
    │   └───zzim
    ├───dto
    │   ├───request
    │   │   ├───admin
    │   │   │   └───category
    │   │   ├───password
    │   │   └───qna
    │   └───response
    │       ├───admin
    │       │   └───category
    │       ├───cart
    │       ├───item
    │       ├───notification
    │       ├───order
    │       │   └───payment
    │       ├───review
    │       ├───seller
    │       └───user
    ├───exception
    │   └───exceptions
    ├───security
    │   └───interceptor
    └───service
        ├───common
        └───handler
```
</details>

# System Design
![Project Architecture](https://user-images.githubusercontent.com/99468424/180926502-71e51d5c-e53a-4b5b-b2d0-c58da550895c.jpg)

# REST API Documents
![API 문서화](https://user-images.githubusercontent.com/99468424/181443761-41cacb8f-0a9a-42c9-9b0c-643daddc450e.png)

# ERD
![erd](https://user-images.githubusercontent.com/99468424/181444923-9210aec4-6053-41d9-ac9f-1a6f78dc9b66.png)
