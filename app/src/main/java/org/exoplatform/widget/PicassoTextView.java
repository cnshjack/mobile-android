/*
 * Copyright (C) 2003-2015 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
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
package org.exoplatform.widget;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.utils.image.ExoPicasso;

import com.squareup.picasso.Target;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by The eXo Platform SAS for use as target to load bitmap by Picasso
 * lib, auto cancel request when detached
 * 
 * @author MinhTDH MinhTDH@exoplatform.com Jul 31, 2015
 */
public class PicassoTextView extends TextView {

  private List<Target> mTargets;

  public PicassoTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public PicassoTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PicassoTextView(Context context) {
    super(context);
  }

  @Override
  protected void onDetachedFromWindow() {
    if (mTargets != null) {
      for (Target target : mTargets) {
        if (target != null) {
          ExoPicasso.picasso(getContext()).cancelRequest(target);
        }
      }
    }
    super.onDetachedFromWindow();
  }

  public void addTarget(Target target) {
    if (mTargets == null) {
      mTargets = new ArrayList<Target>();
    }
    mTargets.add(target);
  }
}
