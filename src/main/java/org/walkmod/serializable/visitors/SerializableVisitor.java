/*
 Copyright (C) 2015 Raquel Pau.
 
Walkmod is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Walkmod is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package org.walkmod.serializable.visitors;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ParseException;
import org.walkmod.javalang.ast.SymbolData;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.type.ClassOrInterfaceType;
import org.walkmod.javalang.compiler.symbols.RequiresSemanticAnalysis;
import org.walkmod.javalang.compiler.symbols.SymbolType;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;

@RequiresSemanticAnalysis
public class SerializableVisitor<A> extends VoidVisitorAdapter<A> {

	@Override
	public void visit(ClassOrInterfaceDeclaration d, A ctx) {
		if (!d.isInterface()) {
			SymbolData sd = d.getSymbolData();
			if (sd != null) {
				Class<?> clazz = sd.getClazz();
				if (clazz != null && !Serializable.class.isAssignableFrom(clazz)) {
					List<ClassOrInterfaceType> implementsList = d.getImplements();
					if (implementsList == null) {
						implementsList = new LinkedList<ClassOrInterfaceType>();
					}
					try {
						ClassOrInterfaceType serializableType = (ClassOrInterfaceType) ASTManager
								.parse(ClassOrInterfaceType.class, "java.io.Serializable");

						serializableType.setSymbolData(new SymbolType(Serializable.class));
						implementsList.add(serializableType);
						d.setImplements(implementsList);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		super.visit(d, ctx);
	}
}
