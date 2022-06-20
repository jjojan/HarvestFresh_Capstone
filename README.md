Capstone Project- README Template
===

# HarvestFresh

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
HarvestFresh allows people to connect with local farmers and farmer's markets in their community. You can easily discover new pop-up markets, buy fresh local produce and sustainably support farm to table food.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Buy/Sell App
- **Mobile:** Mobile only
- **Story:** Allows users to buy fresh local produce and discover local farms.
- **Market:** Anyone interested interested in health concious or farm to table food. Anyone to looking make their grocery run more fun and enviromentally friendly and like farmers markets.
- **Habit:** Users have recurring purchases of groceries and can attend regular farmer's markets.
- **Scope:** Focus on buying produce and discovering new local farms/markets

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can view selection of farmer shopfronts
* User can add items to cart to buy
* User can use Google Maps to view Farms and Markets nearby
* User can view cart


**Optional Nice-to-have Stories**

* Inventory mangement base
* Database storing
* Put ins/reservations for events
* See upcoming events tab

### 2. Screen Archetypes

* Browse
   * See farmers shops
   * Buy/Add to Cart
* Store details
   * Shows all Items
   * Buy/remove
* Map
   * See farmers markets
* Events tab
   * show upcoming events
* Cart tab
   * see cart
   * Checkout
   * remove cart

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Browsw
* Cart
* Calendar
* Map

**Flow Navigation** (Screen to Screen)

* All tabs on bottom toolbar
* Brosw tab to store details tab

## Wireframes

![image](https://user-images.githubusercontent.com/68476473/173660803-d1738400-ca86-4304-b5b4-573148d1d97a.png)

![image](https://user-images.githubusercontent.com/68476473/173660829-53f0067f-bb6c-4524-a8ac-223dbcf9f532.png)



### [BONUS] Digital Wireframes & Mockups

<img width="1375" alt="Screen Shot 2022-06-20 at 2 29 27 PM" src="https://user-images.githubusercontent.com/68476473/174680099-06e39e0e-9bbc-4d32-a46c-38ea0b8e8f8c.png">


### [BONUS] Interactive Prototype

https://previewer.adalo.com/b44d0d5a-8128-47bc-8a2e-08f118492007

## Schema 

## Schema 
### Models
#### StoreFront

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | Farmer      | Pointer   | unique id for the Farmer (default field) |
   | StoreName        | String| name of store |
   | StoreImage        | File| ImagePic |
   | products         | ArrayList     | all products being sold |
   | lat       | long   | lat location |
   | lang | long   | lang position |
   | likesCount    | Number   | number of likes for the store |
   
### CRUD
   | Action      | Description| 
   | ------------- | -------- |
   | Create      | Create new StoreFront   |
   | Read        | get products/location|
   | Update        | update products|
   | Destor        | delete store|
   
 #### Cart

   | Item      | Type     | Description |
   | ------------- | -------- | ------------|
   | Product      | Array   | product food/name |
   | Cost        | Long| cost |

### CRUD
   | Action      | Description| 
   | ------------- | -------- |
   | Create      | Create new cart   |
   | Read        | display products/price for checkout|
   | Update        | update cart|
   | Destor        | delete cart|
   
### Models
[Add table of models]
### Networking
TBA
