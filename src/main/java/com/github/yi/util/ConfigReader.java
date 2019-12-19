package com.github.yi.util;

import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.constretto.model.ClassPathResource;

public class ConfigReader {

	/**
	 * 指定したファイルが存在するか確認する
	 *
	 * @param configFileName
	 *            クラスパスが通った場所に置いてあるプロパティファイル名
	 * @return ファイルがある場合はtrueを返す
	 */
	public static boolean configExits(final String configFileName) {
		return new ClassPathResource(configFileName).exists();
	}

	/**
	 * コンフィグファイルを読み込んでオブジェクト化する関数。 classとコンフィグ名の設定は http://constretto.org/ を参考に
	 *
	 * @param configFileName
	 *            クラスパスが通った場所に置いてあるプロパティファイル名
	 * @param cls
	 *            class to instantiate. コンフィグとメンバーを関連付けしたclass
	 * @return コンフィグオブジェクト
	 */

	public static <T> T readConfig(final String configFileName, final Class<T> cls) {
		final ConstrettoConfiguration configuration = new ConstrettoBuilder().createPropertiesStore()
				.addResource(new ClassPathResource(configFileName)).done().getConfiguration();
		if (configuration == null) {
			return null;
		}
		return configuration.as(cls);
	}
}
