package me.anon.grow3.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.plusAssign
import androidx.viewbinding.ViewBinding

/**
 * Finds the last instance of a type
 */
public inline fun <reified J> List<Any?>.lastInstanceOf(): J? = this.lastOrNull { it is J } as? J
public inline fun <reified J> List<Any?>.lastInstanceOf(additionalPredicate: (item: J) -> Boolean): J? = this.lastOrNull { it is J && additionalPredicate(it) } as? J

/**
 * Returns a de-duplicated list by the query predicate
 */
public inline fun <T, R> List<T>.uniqueBy(crossinline predicate: (T) -> R): List<T>
{
	val list = mutableListOf<R>()
	val subList = mutableListOf<T>()
	this.forEach {
		val selector = predicate(it)
		if (!list.contains(selector))
		{
			subList.add(it)
			list.add(selector)
		}
	}

	return subList
}

/**
 * Loops over an iterable, two elements at a time
 */
public inline fun <T> Iterable<T>.forEachPair(action: (T, T?) -> Unit): Unit
{
	val count = count()
	for (index in 0 until count() step 2) action(elementAt(index), elementAtOrNull(index + 1))
}

/**
 * Maps each iterable entry to a view
 */
public inline fun <T, reified J : ViewBinding> Iterable<T>.mapToView(container: View, crossinline mapper: (T, J) -> Unit): View
{
	forEach {
		val viewBinder = J::class.java
			.getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
			.invoke(null, LayoutInflater.from(container.context), container, false) as ViewBinding
		val viewBinding = viewBinder as J

		mapper(it, viewBinding)
		if (container is ViewGroup) container += viewBinding.root
	}
	return container
}
