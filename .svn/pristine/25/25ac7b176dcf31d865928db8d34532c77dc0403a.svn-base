package com.duowan.yy.titan.cloud.redis;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RedisUtils {

	private RedisUtils() {
	}

	public static final String COLON = ":";

	/**
	 * Builds a namespaced Redis key with the given arguments.
	 * 
	 * @param namespace
	 *            the namespace to use
	 * @param parts
	 *            the key parts to be joined
	 * @return an assembled String key
	 */
	public static String createKey(final String namespace, final String... parts)
	{
		return createKey(namespace, Arrays.asList(parts));
	}

	/**
	 * Builds a namespaced Redis key with the given arguments.
	 * 
	 * @param namespace
	 *            the namespace to use
	 * @param parts
	 *            the key parts to be joined
	 * @return an assembled String key
	 */
	public static String createKey(final String namespace, final Iterable<String> parts)
	{
		final List<String> list = new LinkedList<String>();
		list.add(namespace);
		for (final String part : parts)
		{
			list.add(part);
		}
		return join(COLON, list);
	}

	/**
	 * Join the given strings, separated by the given separator.
	 * 
	 * @param sep
	 *            the separator
	 * @param strs
	 *            the strings to join
	 * @return the joined string
	 */
	public static String join(final String sep, final String... strs)
	{
		return join(sep, Arrays.asList(strs));
	}

	/**
	 * Join the given strings, separated by the given separator.
	 * 
	 * @param sep
	 *            the separator
	 * @param strs
	 *            the strings to join
	 * @return the joined string
	 */
	public static String join(final String sep, final Iterable<String> strs)
	{
		final StringBuilder sb = new StringBuilder();
		String s = "";
		for (final String str : strs)
		{
			sb.append(s).append(str);
			s = sep;
		}
		return sb.toString();
	}
}
