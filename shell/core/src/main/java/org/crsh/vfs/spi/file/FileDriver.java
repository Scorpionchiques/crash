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

package org.crsh.vfs.spi.file;

import org.crsh.vfs.spi.FSDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class FileDriver implements FSDriver<File> {

  /** . */
  private final File root;

  public FileDriver(File root) {
    if (root == null) {
      throw new NullPointerException();
    }

    //
    this.root = root;
  }

  public File root() throws IOException {
    return root;
  }

  public String name(File handle) throws IOException {
    return handle.getName();
  }

  public boolean isDir(File handle) throws IOException {
    return handle.isDirectory();
  }

  public Iterable<File> children(File handle) throws IOException {
    return Arrays.asList(handle.listFiles());
  }

  public URL toURL(File handle) throws IOException {
    return handle.toURI().toURL();
  }
}