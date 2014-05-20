package com.navidroid.model.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
	
	public interface Predicate<T> {
		boolean check(T item, int index);
	}
	
	public static <T> List<T> removeAll(List<T> list, Predicate<T> pred) {
		List<T> newList = new ArrayList<T>();
		for (int i = 0; i < list.size(); i++) {			
			if (pred.check(list.get(i), i)) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
	
	public static <T> int indexOf(List<T> list, Predicate<T> pred) {
		for (int i = 0; i < list.size(); i++) {
			if (pred.check(list.get(i), i)) {
				return i;
			}
		}
		return -1;
	}
	
	public static <T> int lastIndexOf(List<T> list, Predicate<T> pred) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (pred.check(list.get(i), i)) {
				return i;
			}
		}
		return -1;
	}
	
	public static <T> T find(List<T> list, Predicate<T> pred) {
		int indexOf = indexOf(list, pred);
		return indexOf == -1 ? null : list.get(indexOf);
	}
	
	public static <T> T findLast(List<T> list, Predicate<T> pred) {
		int lastIndexOf = lastIndexOf(list, pred);
		return lastIndexOf == -1 ? null : list.get(lastIndexOf);
	}
}
