/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012-2013 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.git.xquery;

import static org.exist.git.xquery.Module.FS;

import java.io.File;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.exist.dom.QName;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Add extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("add", Module.NAMESPACE_URI, Module.PREFIX), 
			"", 
			new SequenceType[] { 
                new FunctionParameterSequenceType(
                    "localPath", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Local path"
                ),
                new FunctionParameterSequenceType(
                    "filepattern", 
                    Type.STRING, 
                    Cardinality.ONE_OR_MORE, 
                    "File pattern"
                )
			}, 
			new FunctionReturnSequenceType(
				Type.BOOLEAN, 
				Cardinality.EXACTLY_ONE, 
				"true if success, false otherwise"
			)
		)
	};

	public Add(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

	        Git git = Git.open(new Resource(localPath), FS);
		    
	        AddCommand command = git.add();
	        
	        for (int i = 0; i < args[1].getItemCount(); i++) {
	        	command.addFilepattern(args[1].itemAt(i).getStringValue());
	        }
	        
	        command.call();

	        return BooleanValue.TRUE;
		} catch (Throwable e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                throw new XPathException(this, Module.EXGIT001, cause.getMessage());
            }

            throw new XPathException(this, Module.EXGIT001, e);
		}
	}
}