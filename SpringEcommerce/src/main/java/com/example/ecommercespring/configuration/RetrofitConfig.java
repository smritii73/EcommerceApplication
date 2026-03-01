package com.example.ecommercespring.configuration;

import com.example.ecommercespring.gateway.api.IFakeStoreCategoryApi;

import com.example.ecommercespring.gateway.api.IFakeStoreProductApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class RetrofitConfig {

  @Value("${Fake_Store_Base_Url}")
  private String BaseUrl;

  @Bean
  public Retrofit retrofit() {
      return new Retrofit.Builder()
              .baseUrl(this.BaseUrl)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
  }

  @Bean
  public IFakeStoreCategoryApi fakeStoreCategoryApi(Retrofit retrofit) {
    return retrofit.create(IFakeStoreCategoryApi.class);
  }
  @Bean
  public IFakeStoreProductApi fakeStoreProductApi(Retrofit retrofit) {
    return retrofit.create(IFakeStoreProductApi.class);
  }

}
