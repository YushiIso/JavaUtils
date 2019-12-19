package com.github.yi.scraper.scraping;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.yi.scraper.dto.RegexDef;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Normalizer {
	/**
	 * 定義にそって指定の文字列の置換をします
	 *
	 * @param str
	 *            置換処理を行う文字列
	 * @param def
	 *            定義
	 * @return 定義にそって置換された文字列
	 */
	public static String replace(final String str, final RegexDef def) {
		if (!"replace".equals(def.getMethod())) {
			throw new IllegalArgumentException(
					String.format("can't do method : '%s' . please set method is 'replace'.", def.getMethod()));
		}
		if (str == null) {
			return null;
		}
		log.info("replace string regex={} replacement={} src={}", def.getRegex(), def.getReplacement(), str);
		return str.replaceAll(def.getRegex(), def.getReplacement());
	}

	/**
	 * 定義のスクリプトを実行して、文字列処理をします
	 *
	 * @param str
	 *            文字列処理を行う文字列
	 * @param def
	 *            定義
	 * @return 処理を行った文字列
	 * @throws ScriptException
	 * @throws NoSuchMethodException
	 */
	public static String script(final String str, final RegexDef def) throws ScriptException, NoSuchMethodException {
		if (!"script".equals(def.getMethod())) {
			throw new IllegalArgumentException(
					String.format("can't do method : %s . please set method is 'script'.", def.getMethod()));
		}

		log.info("run normalization script func={} src={}", def.getScriptFunc(), str);
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.eval(def.getScript());
		final Object result = ((Invocable) engine).invokeFunction(def.getScriptFunc(), str);
		if (result == null) {
			return null;
		}
		if (result.toString().isEmpty()) {
			return null;
		}
		return result.toString();
	}

	public static String normalize(final String str, final RegexDef def) throws NoSuchMethodException, ScriptException {
		switch (def.getMethod()) {
		case "replace":
			return replace(str, def);
		case "script":
			return script(str, def);
		default:
			log.warn("method not fount {}", def.getMethod());
			return str;
		}
	}
}
