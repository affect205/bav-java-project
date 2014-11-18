/**
 * @author alexbalu-alpha7@mail.ru
 */

package com.abalyschev.mychat;

import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeskCmdDecoder {
	
	protected static Logger log = LoggerFactory.getLogger(DeskCmdDecoder.class);
	/**
	 * Вызов метода объекта доски. Метод и параметры определяются парсингом команды
	 */
	static void executeCmd(final String cmd, final PaintFrame painterDeskFrm) {
		
		String[] params = cmd.split(":");
		if ( params.length < 1 ) {
			log.info("Wrong cmd string");
			return;
		}
		switch(params[0]) {
			case "drawLine":
				if ( params.length < 3 ) {
					log.info("Wrong params in string");
					return;
				}
				String[] param1 = params[1].split("&");
				String[] param2 = params[2].split("&");
				
				// вызываем метод
				log.info("Call drawLine method");
				Point pnt1 = new Point(Integer.valueOf(param1[0]).intValue(), Integer.valueOf(param1[1]).intValue());
				Point pnt2 = new Point(Integer.valueOf(param2[0]).intValue(), Integer.valueOf(param2[1]).intValue());
				painterDeskFrm.drawLine(pnt1, pnt2);
			
			case "setLineColor":
				if ( params.length < 2 ) {
					log.info("Wrong params in string");
					return;
				}
				
				// вызываем метод
				log.info("Call setLineColor method");
				painterDeskFrm.setLineColor(params[1]);
		}
	}
}