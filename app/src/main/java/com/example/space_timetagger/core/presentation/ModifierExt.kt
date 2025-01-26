package com.example.space_timetagger.core.presentation

import androidx.compose.ui.Modifier

fun Modifier.thenIf(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: (Modifier.() -> Modifier)? = null,
): Modifier {
    return then(
        if (condition) {
            ifTrue(Modifier)
        } else {
            ifFalse?.invoke(Modifier) ?: Modifier
        }
    )
}