package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWareHouseRequest;

public class WarehouseServiceImpl implements WarehouseService {

    @Override
    public void createProduct(NewProductInWareHouseRequest newProduct) {

    }

    @Override
    public BookedProductsDto checkShoppingCart(ShoppingCartDto shoppingCartDto) {
        return null;
    }
    @Override
    public void addAndChangeQuantityProduct(AddProductToWarehouseRequest productRequest){

    }
    @Override
    public AddressDto getAddress(){
        return null;

    }
}
