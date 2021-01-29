/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.buildbattle.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

//Class used to split players into pairs of 2 for teams game mode
public final class Partition<T> extends AbstractList<List<T>> {

  private final List<T> list;
  private final int chunkSize;

  public Partition(List<T> list, int chunkSize) {
    this.list = new ArrayList<>(list);
    this.chunkSize = chunkSize;
  }

  public static <T> Partition<T> ofSize(List<T> list, int chunkSize) {
    return new Partition<>(list, chunkSize);
  }

  @Override
  public List<T> get(int index) {
    int start = index * chunkSize;
    int end = Math.min(start + chunkSize, list.size());

    if(start > end) {
      throw new IndexOutOfBoundsException("Index " + index + " is out of the list range <0," + (size() - 1) + ">");
    }

    return new ArrayList<>(list.subList(start, end));
  }

  @Override
  public int size() {
    return (int) Math.ceil((double) list.size() / (double) chunkSize);
  }
}
