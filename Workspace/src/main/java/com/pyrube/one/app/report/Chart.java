/*******************************************************************************
 * Copyright 2019, 2023 Aranjuez Poon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pyrube.one.app.report;

import java.io.Serializable;
import java.util.Arrays;

import com.pyrube.one.app.persistence.Data;

/**
 * <code>Chart</code> data.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Chart extends Data<String> {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 3078771501688184129L;

	/**
	 * this <code>Chart</code>
	 */
	private String id;
	private String title;
	private String type;
	private Axis xaxis;
	private Axis yaxis;
	private Dataitem[] dataset;

	/**
	 * constructor
	 */
	public Chart() { }

	/**
	 * @param title the title to set
	 */
	public Chart title(String title) {
		this.title = title;
		return(this);
	}

	/**
	 * adds a data item into dataset
	 * @param label
	 * @param data
	 */
	public Chart dataitem(String label, Number[] data) {
		return(this.dataitem(label, data, null));
	}

	/**
	 * adds a data item into dataset
	 * @param label
	 * @param data
	 * @param format
	 */
	public Chart dataitem(String label, Number[] data, String format) {
		return(this.dataitem(label, data, format, null));
	}

	/**
	 * adds a data item into dataset
	 * @param label
	 * @param data
	 * @param format
	 * @param ccy
	 */
	public Chart dataitem(String label, Number[] data, String format, String ccy) {
		if (this.dataset == null) this.dataset = new Dataitem[0];
		this.dataset = Arrays.copyOf(dataset, dataset.length + 1);
		this.dataset[dataset.length - 1] = new Dataitem(label, data, format, ccy);
		return(this);
	}

	/**
	 * generate the x-axis
	 * @param ticks
	 */
	public Chart xaxis(Object[] ticks) {
		return(this.xaxis(ticks, null));
	}

	/**
	 * generate the x-axis
	 * @param ticks
	 * @param label
	 */
	public Chart xaxis(Object[] ticks, String label) {
		return(this.xaxis(ticks, label, null));
	}

	/**
	 * generate the x-axis
	 * @param ticks
	 * @param label
	 * @param format
	 */
	public Chart xaxis(Object[] ticks, String label, String format) {
		if (this.xaxis == null) this.xaxis = new Axis(ticks, label, format);
		else {
			this.xaxis.setLabel(label);
			this.xaxis.setFormat(format);
			this.xaxis.setTicks(ticks);
		}
		return(this);
	}

	/**
	 * generate the y-axis
	 * @param ticks
	 */
	public Chart yaxis(Object[] ticks) {
		return(this.yaxis(ticks, null));
	}

	/**
	 * generate the y-axis
	 * @param ticks
	 * @param format
	 */
	public Chart yaxis(Object[] ticks, String format) {
		if (this.yaxis == null) this.yaxis = new Axis(ticks, null, format);
		else {
			this.yaxis.setFormat(format);
			this.yaxis.setTicks(ticks);
		}
		return(this);
	}

	/**
	 * generate the y-axis
	 * @param tickval
	 */
	public Chart yaxis(Number tickval) {
		return(this.yaxis(0, tickval));
	}

	/**
	 * generate the y-axis
	 * @param base
	 * @param tickval
	 */
	public Chart yaxis(Number base, Number tickval) {
		return(this.yaxis(null, base, tickval));
	}

	/**
	 * generate the y-axis
	 * @param format
	 * @param tickval
	 */
	public Chart yaxis(String format, Number tickval) {
		return(this.yaxis(format, 0, tickval));
	}

	/**
	 * generate the y-axis
	 * @param format
	 * @param base
	 * @param tickval
	 */
	public Chart yaxis(String format, Number base, Number tickval) {
		return(this.yaxis(format, null, base, tickval));
	}

	/**
	 * generate the y-axis
	 * @param format
	 * @param unit
	 * @param tickval
	 */
	public Chart yaxis(String format, String unit, Number tickval) {
		return(this.yaxis(format, unit, 0, tickval));
	}

	/**
	 * generate the y-axis
	 * @param format
	 * @param unit
	 * @param base
	 * @param tickval
	 */
	public Chart yaxis(String format, String unit, Number base, Number tickval) {
		return(this.yaxis(format, unit, null, base, tickval));
	}

	/**
	 * generate the y-axis
	 * @param format
	 * @param unit
	 * @param ccy
	 * @param tickval
	 */
	public Chart yaxis(String format, String unit, String ccy, Number tickval) {
		return(this.yaxis(format, unit, ccy, 0, tickval));
	}

	/**
	 * generate the y-axis
	 * @param format
	 * @param unit
	 * @param ccy
	 * @param base
	 * @param tickval
	 */
	public Chart yaxis(String format, String unit, String ccy, Number base, Number tickval) {
		if (this.yaxis == null) this.yaxis = new Axis(format, unit, ccy, base, tickval);
		else {
			this.yaxis.setFormat(format);
			this.yaxis.setUnit(unit);
			this.yaxis.setCcy(ccy);
			this.yaxis.setBase(base);
			this.yaxis.setTickval(tickval);
		}
		return(this);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the xaxis
	 */
	public Axis getXaxis() {
		return xaxis;
	}

	/**
	 * @param xaxis the xaxis to set
	 */
	public void setXaxis(Axis xaxis) {
		this.xaxis = xaxis;
	}

	/**
	 * @return the yaxis
	 */
	public Axis getYaxis() {
		return yaxis;
	}

	/**
	 * @param yaxis the yaxis to set
	 */
	public void setYaxis(Axis yaxis) {
		this.yaxis = yaxis;
	}

	/**
	 * @return the dataset
	 */
	public Dataitem[] getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(Dataitem[] dataset) {
		this.dataset = dataset;
	}

	/**
	 * <code>Dataitem</code> data for Chart.
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public class Dataitem implements Serializable {
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 7619811529656569343L;
		private String label;
		private Number[] data;
		private String format;
		private String ccy;
		/**
		 * constructor
		 * @param label
		 * @param data
		 */
		public Dataitem(String label, Number[] data) {
			this(label, data, null);
		}
		/**
		 * constructor
		 * @param label
		 * @param data
		 * @param format
		 */
		public Dataitem(String label, Number[] data, String format) {
			this(label, data, format, null);
		}
		/**
		 * constructor
		 * @param label
		 * @param data
		 * @param format
		 * @param ccy
		 */
		public Dataitem(String label, Number[] data, String format, String ccy) {
			this.label  = label;
			this.data   = data;
			this.format = format;
			this.ccy    = ccy;
		}
		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		/**
		 * @return the data
		 */
		public Number[] getData() {
			return data;
		}
		/**
		 * @param data the data to set
		 */
		public void setData(Number[] data) {
			this.data = data;
		}
		/**
		 * @return the format
		 */
		public String getFormat() {
			return format;
		}
		/**
		 * @param format the format to set
		 */
		public void setFormat(String format) {
			this.format = format;
		}
		/**
		 * @return the ccy
		 */
		public String getCcy() {
			return ccy;
		}
		/**
		 * @param ccy the ccy to set
		 */
		public void setCcy(String ccy) {
			this.ccy = ccy;
		}
	}

	/**
	 * <code>Axis</code> data for Chart.
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public class Axis implements Serializable {
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 8151569235204506170L;
		private String name;
		private Object[] ticks;
		private String label;
		private String format;
		private String unit;
		private String ccy;
		private Number base;
		private Number tickval;
		private Number end;
		private Number max;
		/**
		 * constructor
		 * @param ticks
		 */
		public Axis(Object[] ticks) {
			this(ticks, null);
		}
		/**
		 * constructor
		 * @param ticks
		 * @param label
		 */
		public Axis(Object[] ticks, String label) {
			this(ticks, label, null);
		}
		/**
		 * constructor
		 * @param ticks
		 * @param label
		 * @param format
		 */
		public Axis(Object[] ticks, String label, String format) {
			this.ticks  = ticks;
			this.label  = label;
			this.format = format;
		}
		/**
		 * constructor
		 * @param format
		 * @param tickval
		 */
		public Axis(String format, Number tickval) {
			this(format, null, null, 0, tickval);
		}
		/**
		 * constructor
		 * @param format
		 * @param base
		 * @param tickval
		 */
		public Axis(String format, Number base, Number tickval) {
			this(format, null, null, base, tickval);
		}
		/**
		 * constructor
		 * @param format
		 * @param unit
		 * @param tickval
		 */
		public Axis(String format, String unit, Number tickval) {
			this(format, unit, null, 0, tickval);
		}
		/**
		 * constructor
		 * @param format
		 * @param unit
		 * @param base
		 * @param tickval
		 */
		public Axis(String format, String unit, Number base, Number tickval) {
			this(format, unit, null, base, tickval);
		}
		/**
		 * constructor
		 * @param format
		 * @param unit
		 * @param ccy
		 * @param tickval
		 */
		public Axis(String format, String unit, String ccy, Number tickval) {
			this(format, unit, ccy, 0, tickval);
		}
		/**
		 * constructor
		 * @param format
		 * @param unit
		 * @param ccy
		 * @param base
		 * @param tickval
		 */
		public Axis(String format, String unit, String ccy, Number base, Number tickval) {
			this.format  = format;
			this.unit = unit;
			this.ccy = ccy;
			this.base   = base;
			this.tickval = tickval;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the ticks
		 */
		public Object[] getTicks() {
			return ticks;
		}
		/**
		 * @param ticks the ticks to set
		 */
		public void setTicks(Object[] ticks) {
			this.ticks = ticks;
		}
		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		/**
		 * @return the format
		 */
		public String getFormat() {
			return format;
		}
		/**
		 * @param format the format to set
		 */
		public void setFormat(String format) {
			this.format = format;
		}
		/**
		 * @return the unit
		 */
		public String getUnit() {
			return unit;
		}
		/**
		 * @param unit the unit to set
		 */
		public void setUnit(String unit) {
			this.unit = unit;
		}
		/**
		 * @return the ccy
		 */
		public String getCcy() {
			return ccy;
		}
		/**
		 * @param ccy the ccy to set
		 */
		public void setCcy(String ccy) {
			this.ccy = ccy;
		}
		/**
		 * @return the base
		 */
		public Number getBase() {
			return base;
		}
		/**
		 * @param base the base to set
		 */
		public void setBase(Number base) {
			this.base = base;
		}
		/**
		 * @return the tickval
		 */
		public Number getTickval() {
			return tickval;
		}
		/**
		 * @param tickval the tickval to set
		 */
		public void setTickval(Number tickval) {
			this.tickval = tickval;
		}
		/**
		 * @return the end
		 */
		public Number getEnd() {
			return end;
		}
		/**
		 * @param end the end to set
		 */
		public void setEnd(Number end) {
			this.end = end;
		}
		/**
		 * @return the max
		 */
		public Number getMax() {
			return max;
		}
		/**
		 * @param max the max to set
		 */
		public void setMax(Number max) {
			this.max = max;
		}
		
	}

}
