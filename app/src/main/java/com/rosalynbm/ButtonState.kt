package com.rosalynbm

enum class ButtonState {
    CLICKED,
    LOADING,
    COMPLETED
}

/*
// Sealed classes should be preferred only if the class is more complex and
// some of its objects may contain data
sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}*/
