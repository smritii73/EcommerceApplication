package com.example.ecommercespring.gateway;

import com.example.ecommercespring.dto.FakeStoreProductResponseDTO;
import com.example.ecommercespring.dto.ProductDTO;
import com.example.ecommercespring.gateway.api.IFakeStoreProductApi;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class FakeStoreProductGateway implements IProductGateway{
    private final IFakeStoreProductApi fakeStoreProductApi;
    FakeStoreProductGateway(IFakeStoreProductApi fakeStoreProductApi) {
        this.fakeStoreProductApi = fakeStoreProductApi;
    }
    @Override
    public ProductDTO getProductById(Long id) throws IOException {
        FakeStoreProductResponseDTO response = this.fakeStoreProductApi.getFakeProduct(id).execute().body();
        if(response == null){
            throw new IOException("Product not found");
        }
        return response.getProduct();
    }

}
