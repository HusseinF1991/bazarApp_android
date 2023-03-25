package com.uruksys.bazarapp;

import java.util.ArrayList;
import java.util.Date;



//categories model
class CategoriesMenuModel {
    private int categoryId, parentCategoryId, categoryLevel;
    private String categoryTitle, parentCatTitle;

    public CategoriesMenuModel(int categoryId, int categoryLevel, String categoryTitle, int parentCategoryId, String parentCatTitle) {
        this.categoryId = categoryId;
        this.parentCategoryId = parentCategoryId;
        this.categoryLevel = categoryLevel;
        this.categoryTitle = categoryTitle;
        this.parentCatTitle = parentCatTitle;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getParentCategoryId() {
        return parentCategoryId;
    }

    public int getCategoryLevel() {
        return categoryLevel;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getParentCatTitle() {
        return parentCatTitle;
    }
}


class BrandsModel {

    String brandName, brandLogo, description;
    int id;

    public BrandsModel(int id, String brandName, String brandLogo, String description) {
        this.id = id;
        this.brandName = brandName;
        this.brandLogo = brandLogo;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public String getDescription() {
        return description;
    }
}


class ProvidersNShopsModel {

    private int id;
    private String name;
    private String logo;
    private String specialty;
    private String email;
    private String location;
    private String lat;
    private String lng;

    public ProvidersNShopsModel(int id, String name, String logo, String specialty, String email, String location, String lat, String lng) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.specialty = specialty;
        this.email = email;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}


class ItemsInInvoiceShopModel {

    private int invoiceItemId, typeId;
    private String itemCode, itemName, typeName, imageLoc;
    private double sellPrice , qty;

    public ItemsInInvoiceShopModel(int invoiceItemId, int typeId, String itemCode,
                                   String itemName, String typeName, String imageLoc, double sellPrice, double qty) {
        this.invoiceItemId = invoiceItemId;
        this.typeId = typeId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.typeName = typeName;
        this.imageLoc = imageLoc;
        this.sellPrice = sellPrice;
        this.qty = qty;
    }

    public int getInvoiceItemId() {
        return invoiceItemId;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getImageLoc() {
        return imageLoc;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getQty() {
        return qty;
    }
}


class ItemsModel {
    private int itemTypeId;
    private int itemId;
    private String typeName;
    private double availableQty;
    private double discountPrice;
    private double sellPrice;
    private String itemCode;
    private String itemName;
    private double avgRate;
    private String imageLoc;


    public ItemsModel(int itemTypeId, int itemId, String typeName, double availableQty, double discountPrice, double sellPrice,
                      String itemCode, String itemName, double avgRate, String imageLoc) {
        this.itemTypeId = itemTypeId;
        this.itemId = itemId;
        this.typeName = typeName;
        this.availableQty = availableQty;
        this.discountPrice = discountPrice;
        this.sellPrice = sellPrice;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.avgRate = avgRate;
        this.imageLoc = imageLoc;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getAvailableQty() {
        return availableQty;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public double getAvgRate() {
        return avgRate;
    }

    public String getImageLoc() {
        return imageLoc;
    }
}


class ItemDetailsModel {
    private int itemId;
    private String itemCode;
    private String itemName;
    private int shopId;
    private String shopName;
    private String shopLogo;
    private String shopSpecialty;
    private int categoryId;
    private String catName;
    private String itemCreatedAt;
    private String itemUpdatedAt;
    private String brandId;
    private String brandName;
    private String brandLogo;
    private String brandDescription;
    private ArrayList<ItemTypeDetailsModel> itemTypeDetailsArrayList;
    private ArrayList<ItemReviews> itemReviewsArrayList;
    private double avgRate;

    public ItemDetailsModel(int itemId, String itemCode, String itemName, int shopId, String shopName, String shopLogo, String shopSpecialty,
                            int categoryId, String catName, String itemCreatedAt, String itemUpdatedAt, String brandId, String brandName,
                            String brandLogo, String brandDescription, ArrayList<ItemTypeDetailsModel> itemTypeDetailsArrayList,
                            ArrayList<ItemReviews> itemReviewsArrayList, double avgRate) {
        this.itemId = itemId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopLogo = shopLogo;
        this.shopSpecialty = shopSpecialty;
        this.categoryId = categoryId;
        this.catName = catName;
        this.itemCreatedAt = itemCreatedAt;
        this.itemUpdatedAt = itemUpdatedAt;
        this.brandId = brandId;
        this.brandName = brandName;
        this.brandLogo = brandLogo;
        this.brandDescription = brandDescription;
        this.itemTypeDetailsArrayList = itemTypeDetailsArrayList;
        this.itemReviewsArrayList = itemReviewsArrayList;
        this.avgRate = avgRate;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public String getShopSpecialty() {
        return shopSpecialty;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCatName() {
        return catName;
    }

    public String getItemCreatedAt() {
        return itemCreatedAt;
    }

    public String getItemUpdatedAt() {
        return itemUpdatedAt;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public String getBrandDescription() {
        return brandDescription;
    }

    public ArrayList<ItemTypeDetailsModel> getItemTypeDetailsArrayList() {
        return itemTypeDetailsArrayList;
    }

    public ArrayList<ItemReviews> getItemReviewsArrayList() {
        return itemReviewsArrayList;
    }

    public double getAvgRate() {
        return avgRate;
    }
}


class ItemTypeDetailsModel {
    private int typeId;
    private int itemId;
    private String typeName;
    private double availableQty;
    private double sellPrice;
    private double discountPrice;
    private String description;
    private String expDate;
    private String typeCreatedAt;
    private String typeUpdatedAt;
    private String lastSellDate;
    private int numOfSells;
    private ArrayList<String> itemTypeImagesArrayList;


    public ItemTypeDetailsModel(int typeId, int itemId, String typeName, double availableQty, double sellPrice, double discountPrice,
                                String description, String expDate, String typeCreatedAt, String typeUpdatedAt, String lastSellDate,
                                int numOfSells, ArrayList<String> itemTypeImagesArrayList) {
        this.typeId = typeId;
        this.itemId = itemId;
        this.typeName = typeName;
        this.availableQty = availableQty;
        this.sellPrice = sellPrice;
        this.discountPrice = discountPrice;
        this.description = description;
        this.expDate = expDate;
        this.typeCreatedAt = typeCreatedAt;
        this.typeUpdatedAt = typeUpdatedAt;
        this.lastSellDate = lastSellDate;
        this.numOfSells = numOfSells;
        this.itemTypeImagesArrayList = itemTypeImagesArrayList;
    }

    public String getLastSellDate() {
        return lastSellDate;
    }

    public int getNumOfSells() {
        return numOfSells;
    }

    public void setAvailableQty(double availableQty) {
        this.availableQty = availableQty;
    }

    public ArrayList<String> getItemTypeImagesArrayList() {
        return itemTypeImagesArrayList;
    }

    public int getTypeId() {
        return typeId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getAvailableQty() {
        return availableQty;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public String getDescription() {
        return description;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getTypeCreatedAt() {
        return typeCreatedAt;
    }

    public String getTypeUpdatedAt() {
        return typeUpdatedAt;
    }
}


class ItemReviews {
    private int reviewId;
    private int itemId;
    private String reviewerName;
    private String customerId;
    private String title;
    private String body;
    private double rate;
    private String createdAt;

    public ItemReviews(int reviewId, int itemId, String reviewerName, String customerId, String title, String body, double rate, String createdAt) {
        this.reviewId = reviewId;
        this.itemId = itemId;
        this.reviewerName = reviewerName;
        this.customerId = customerId;
        this.title = title;
        this.body = body;
        this.rate = rate;
        this.createdAt = createdAt;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public double getRate() {
        return rate;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}


class ItemsInCartModel {

    private int itemId, itemTypeId ,shopId;
    private String itemCode, itemName, imageLoc , typeName;
    private double sellPrice , discountPrice , availableQty, qtyInCart,shopPurchasePrice;


    public ItemsInCartModel(int itemId, int itemTypeId, String itemCode, String itemName, int shopId, String imageLoc, String typeName,
                            double shopPurchasePrice, double sellPrice, double discountPrice, double availableQty, double qtyInCart) {
        this.itemId = itemId;
        this.itemTypeId = itemTypeId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.imageLoc = imageLoc;
        this.typeName = typeName;
        this.shopPurchasePrice = shopPurchasePrice;
        this.sellPrice = sellPrice;
        this.discountPrice = discountPrice;
        this.availableQty = availableQty;
        this.qtyInCart = qtyInCart;
        this.shopId = shopId;
    }

    public double getShopPurchasePrice() {
        return shopPurchasePrice;
    }

    public int getShopId() {
        return shopId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemTypeId() {
        return itemTypeId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getImageLoc() {
        return imageLoc;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public double getAvailableQty() {
        return availableQty;
    }

    public double getQtyInCart() {
        return qtyInCart;
    }
}


class ItemReviewsModel {

    private int id;
    private double rate;
    private String title, reviewerName, createdAt, body;

    public ItemReviewsModel(int id, double rate, String title, String reviewerName, String createdAt, String body) {
        this.id = id;
        this.rate = rate;
        this.title = title;
        this.reviewerName = reviewerName;
        this.createdAt = createdAt;
        this.body = body;
    }


    public int getId() {
        return id;
    }

    public double getRate() {
        return rate;
    }

    public String getTitle() {
        return title;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }
}



class InvoiceModel {
    private int invoiceId;
    private String CreatedAt;
    private double invoiceTotalCost;
    private double invoiceTotalPaidAmount;
    private int invoiceTotalItems;
    private ArrayList<ShopInvoiceModel> shopInvoiceArrayList;
    private String invoiceStatusOverall;


    public InvoiceModel(int invoiceId, String createdAt, double invoiceTotalCost, double invoiceTotalPaidAmount, int invoiceTotalItems,
                        String invoiceStatusOverall, ArrayList<ShopInvoiceModel> shopInvoiceArrayList) {
        this.invoiceId = invoiceId;
        CreatedAt = createdAt;
        this.invoiceTotalCost = invoiceTotalCost;
        this.invoiceTotalPaidAmount = invoiceTotalPaidAmount;
        this.invoiceTotalItems = invoiceTotalItems;
        this.invoiceStatusOverall = invoiceStatusOverall;
        this.shopInvoiceArrayList = shopInvoiceArrayList;
    }

    public double getInvoiceTotalPaidAmount() {
        return invoiceTotalPaidAmount;
    }

    public String getInvoiceStatusOverall() {
        return invoiceStatusOverall;
    }

    public ArrayList<ShopInvoiceModel> getShopInvoiceArrayList() {
        return shopInvoiceArrayList;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public double getInvoiceTotalCost() {
        return invoiceTotalCost;
    }

    public int getInvoiceTotalItems() {
        return invoiceTotalItems;
    }
}


class ShopInvoiceModel {
    private int shopInvoiceId;
    private int shopId;
    private double totalCost;
    private double paidAmount;
    private String paymentMethod;
    private String status;
    private String shopName;
    private String mobile;
    private int itemsCount;

    public ShopInvoiceModel(int shopInvoiceId, int shopId, double totalCost, double paidAmount, String paymentMethod, String status, String shopName
            , String mobile, int itemsCount) {
        this.shopInvoiceId = shopInvoiceId;
        this.shopId = shopId;
        this.totalCost = totalCost;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.shopName = shopName;
        this.mobile = mobile;
        this.itemsCount = itemsCount;
    }


    public String getMobile() {
        return mobile;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public int getShopInvoiceId() {
        return shopInvoiceId;
    }

    public int getShopId() {
        return shopId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public String getShopName() {
        return shopName;
    }
}


class InvoiceShopChatModel {

    private int id, invoiceShopId , isRead;
    private String msgDate;
    private String createdAt, sender;

    public InvoiceShopChatModel(int id, int invoiceShopId, String createdAt, String sender, String msgDate , int isRead) {
        this.id = id;
        this.invoiceShopId = invoiceShopId;
        this.msgDate = msgDate;
        this.createdAt = createdAt;
        this.sender = sender;
        this.isRead = isRead;
    }


    public int getId() {
        return id;
    }

    public int getInvoiceShopId() {
        return invoiceShopId;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSender() {
        return sender;
    }
}

