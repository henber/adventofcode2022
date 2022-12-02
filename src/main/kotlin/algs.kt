import java.math.BigInteger

// Implementation of some algorithms in kotlin, "stolen" from https://github.com/andrewass/kalgos/tree/master/src/main/kotlin/algorithms
/**
 * [Chinese Remainder Theorem](https://en.wikipedia.org/wiki/Chinese_remainder_theorem)
 *
 * @param vals List of numbers serving as the remainder values
 * @param modVals List of co-prime divisors
 * @return the remainder satisfying all equations
 */
fun crm(vals: List<Long>, modVals: List<Long>): Long {
    val modProduct = modVals.product()
    var res = 0L
    for (i in vals.indices) {
        val gcd = extendedEuclid(modVals[i], modProduct / modVals[i])
        val a = BigInteger(vals[i].toString())
        val b = BigInteger(gcd[2].toString())
        val c = BigInteger((modProduct / modVals[i]).toString())
        val mod = BigInteger(modProduct.toString())
        res += a.multiply(b).multiply(c).remainder(mod).toLong()
        res = (res.rem(modProduct) + modProduct).rem(modProduct)
    }
    return res
}

/**
 * [Extended Euclidean algorithm](https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm)
 * Calculates both the gcd(a,b) and ax + by = gcd(a,b)
 *
 * @param a First number
 * @param b Second number
 * @return an array containing in given order the gcd d, and the coefficients x and y
 */
fun extendedEuclid(a: Long, b: Long): LongArray {
    return if (b == 0L) {
        longArrayOf(a, 1L, 0L)
    } else {
        val res = extendedEuclid(b, a.rem(b))
        longArrayOf(res[0], res[2], res[1] - (a / b) * res[2])
    }
}

/**
 * Find the product of all the numbers in the list
 */
fun <E : Number> List<E>.product(): Long {
    if (this.isEmpty()) {
        return 0L
    }
    var product = 1L
    for (numb in this) {
        when (numb) {
            is Long -> product *= numb
            is Int -> product *= numb
        }
    }
    return product
}