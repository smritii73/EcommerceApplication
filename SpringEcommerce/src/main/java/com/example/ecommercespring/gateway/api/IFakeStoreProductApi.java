package com.example.ecommercespring.gateway.api;

import com.example.ecommercespring.dto.FakeStoreProductResponseDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;

public interface IFakeStoreProductApi {
    @GET("products/{id}")
    Call<FakeStoreProductResponseDTO> getFakeProduct (@Path("id") Long id) throws IOException;
}
