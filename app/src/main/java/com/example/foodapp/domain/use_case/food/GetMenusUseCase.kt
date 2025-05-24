package com.example.foodapp.domain.use_case.food

import androidx.paging.PagingData
import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenusUseCase @Inject constructor(
    private val menuRepository: MenuRepository
) {
    operator fun invoke() :Flow<PagingData<Menu>> {
        return menuRepository.getMenu()
    }

}