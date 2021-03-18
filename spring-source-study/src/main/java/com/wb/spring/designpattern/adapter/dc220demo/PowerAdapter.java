package com.wb.spring.designpattern.adapter.dc220demo;

/**
 * Created by wangbin33 on 2020/2/17.
 */
public class PowerAdapter implements DC5 {
	private AC220 ac220;
	public PowerAdapter(AC220 ac220) {
		this.ac220 = ac220;
	}
	@Override
	public int outputDC5V() {
		int adapterInput = ac220.outputAC220V();
		int adapterOutput = adapterInput / 44;
		System.out.println("使用电压适配器PowerAdapter，输入电压：" + adapterInput + "V，输出电压：" + adapterOutput + "V");
		return adapterOutput;
	}
}
