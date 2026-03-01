package com.example.ecommercespring.gateway.api;
import com.example.ecommercespring.dto.FakeStoreCategoryResponseDTO;
import retrofit2.Call;
import retrofit2.http.GET;

import java.io.IOException;


public interface IFakeStoreCategoryApi {

    @GET("products/category")
    Call<FakeStoreCategoryResponseDTO> getAllFakeCategory() throws IOException;
}


