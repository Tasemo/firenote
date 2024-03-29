package de.oelkers.firenote.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T, F, S : Iterable<T>> LiveData<S>.filter(filterValue: LiveData<F>, filter: (F?, T) -> Boolean): LiveData<List<T>> {
    return MediatorLiveData<List<T>>().apply {
        addSource(filterValue) {
            if (this@filter.value != null) {
                this.value = this@filter.value!!.filter { entry -> filter(it, entry) }
            }
        }
        addSource(this@filter) {
            this.value = it.filter { entry -> filter(filterValue.value, entry) }
        }
    }
}
