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

package com.pyrube.one.lang;

/**
 * the <code>Finalizable</code> interface should be implemented by any 
 * class whose instances take some resources that are intended to be 
 * released finally. the class must define a method of no arguments 
 * called <code>finalize</code>.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public interface Finalizable {
	
	/**
	 * the general contract of the method <code>finalize</code> is that it may
	 * release any resource taken by an object implementing interface
	 * <code>Finalizable</code>.
	 */
	public abstract void finalize();
}