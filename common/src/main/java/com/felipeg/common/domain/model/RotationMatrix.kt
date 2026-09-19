package com.felipeg.common.domain.model

data class RotationMatrix(val values: FloatArray) {
    init {
        require(values.size == MATRIX_SIZE) { "A 3x3 rotation matrix requires 9 values" }
    }

    override fun equals(other: Any?): Boolean =
        other is RotationMatrix && values.contentEquals(other.values)

    override fun hashCode(): Int = values.contentHashCode()

    private companion object {
        const val MATRIX_SIZE = 9
    }
}
