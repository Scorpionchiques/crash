/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.shell.connector;

import org.crsh.shell.ErrorType;
import org.crsh.shell.ShellResponse;

import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class ResponseEvaluation implements Callable<ShellResponse> {

  /** . */
  private final ConnectorResponseContext responseContext;

  /** . */
  private final Callable<ShellResponse> delegate;

  /** . */
  private final Connector connector;

  public ResponseEvaluation(Connector connector, ConnectorResponseContext responseContext, Callable<ShellResponse> delegate) {
    this.connector = connector;
    this.responseContext = responseContext;
    this.delegate = delegate;
  }

  public ShellResponse call() {
    try {
      // Obtain real response
      ShellResponse response = delegate.call();

      // Update connector and get string response
      connector.update(responseContext, response);

      // Return response also
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return new ShellResponse.Error(ErrorType.INTERNAL, e);
    }
  }
}