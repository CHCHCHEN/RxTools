package com.yingda.rxtools

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * author: chen
 * data: 2022/7/19
 * des: 
*/
object RxTextTool {
    /**
     * 获取建造者
     *
     * @param text 样式字符串文本
     * @return [Builder]
     */
    @JvmStatic
    fun getBuilder(text: CharSequence): Builder {
        return Builder(text)
    }

    class Builder internal constructor(private var text: CharSequence) {
        private val defaultValue = 0x12000000
        private var flag: Int

        @ColorInt
        private var foregroundColor: Int

        @ColorInt
        private var backgroundColor: Int

        @ColorInt
        private var quoteColor: Int
        private var isLeadingMargin = false
        private var first = 0
        private var rest = 0
        private var isBullet = false
        private var gapWidth = 0
        private var bulletColor = 0
        private var proportion: Float
        private var xProportion: Float
        private var isStrikethrough = false
        private var isUnderline = false
        private var isSuperscript = false
        private var isSubscript = false
        private var isBold = false
        private var isItalic = false
        private var isBoldItalic = false
        private var fontFamily: String? = null
        private var align: Layout.Alignment? = null
        private var imageIsBitmap = false
        private var bitmap: Bitmap? = null
        private var imageIsDrawable = false
        private var drawable: Drawable? = null
        private var imageIsUri = false
        private var uri: Uri? = null
        private var imageIsResourceId = false

        @DrawableRes
        private var resourceId = 0
        private var clickSpan: ClickableSpan? = null
        private var url: String? = null
        private var isBlur = false
        private var radius = 0f
        private var style: Blur? = null
        private val mBuilder: SpannableStringBuilder

        /**
         * 设置标识
         *
         * @param flag
         *  * [Spanned.SPAN_INCLUSIVE_EXCLUSIVE]
         *  * [Spanned.SPAN_INCLUSIVE_INCLUSIVE]
         *  * [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
         *  * [Spanned.SPAN_EXCLUSIVE_INCLUSIVE]
         *
         * @return [Builder]
         */
        fun setFlag(flag: Int): Builder {
            this.flag = flag
            return this
        }

        /**
         * 设置前景色
         *
         * @param color 前景色
         * @return [Builder]
         */
        fun setForegroundColor(@ColorInt color: Int): Builder {
            foregroundColor = color
            return this
        }

        /**
         * 设置背景色
         *
         * @param color 背景色
         * @return [Builder]
         */
        fun setBackgroundColor(@ColorInt color: Int): Builder {
            backgroundColor = color
            return this
        }

        /**
         * 设置引用线的颜色
         *
         * @param color 引用线的颜色
         * @return [Builder]
         */
        fun setQuoteColor(@ColorInt color: Int): Builder {
            quoteColor = color
            return this
        }

        /**
         * 设置缩进
         *
         * @param first 首行缩进
         * @param rest  剩余行缩进
         * @return [Builder]
         */
        fun setLeadingMargin(first: Int, rest: Int): Builder {
            this.first = first
            this.rest = rest
            isLeadingMargin = true
            return this
        }

        /**
         * 设置列表标记
         *
         * @param gapWidth 列表标记和文字间距离
         * @param color    列表标记的颜色
         * @return [Builder]
         */
        fun setBullet(gapWidth: Int, color: Int): Builder {
            this.gapWidth = gapWidth
            bulletColor = color
            isBullet = true
            return this
        }

        /**
         * 设置字体比例
         *
         * @param proportion 比例
         * @return [Builder]
         */
        fun setProportion(proportion: Float): Builder {
            this.proportion = proportion
            return this
        }

        /**
         * 设置字体横向比例
         *
         * @param proportion 比例
         * @return [Builder]
         */
        fun setXProportion(proportion: Float): Builder {
            xProportion = proportion
            return this
        }

        /**
         * 设置删除线
         *
         * @return [Builder]
         */
        fun setStrikethrough(): Builder {
            isStrikethrough = true
            return this
        }

        /**
         * 设置下划线
         *
         * @return [Builder]
         */
        fun setUnderline(): Builder {
            isUnderline = true
            return this
        }

        /**
         * 设置上标
         *
         * @return [Builder]
         */
        fun setSuperscript(): Builder {
            isSuperscript = true
            return this
        }

        /**
         * 设置下标
         *
         * @return [Builder]
         */
        fun setSubscript(): Builder {
            isSubscript = true
            return this
        }

        /**
         * 设置粗体
         *
         * @return [Builder]
         */
        fun setBold(): Builder {
            isBold = true
            return this
        }

        /**
         * 设置斜体
         *
         * @return [Builder]
         */
        fun setItalic(): Builder {
            isItalic = true
            return this
        }

        /**
         * 设置粗斜体
         *
         * @return [Builder]
         */
        fun setBoldItalic(): Builder {
            isBoldItalic = true
            return this
        }

        /**
         * 设置字体
         *
         * @param fontFamily 字体
         *
         *  * monospace
         *  * serif
         *  * sans-serif
         *
         * @return [Builder]
         */
        fun setFontFamily(fontFamily: String?): Builder {
            this.fontFamily = fontFamily
            return this
        }

        /**
         * 设置对齐
         *
         * @param align 对其方式
         *
         *  * [Alignment.ALIGN_NORMAL]正常
         *  * [Alignment.ALIGN_OPPOSITE]相反
         *  * [Alignment.ALIGN_CENTER]居中
         *
         * @return [Builder]
         */
        fun setAlign(align: Layout.Alignment?): Builder {
            this.align = align
            return this
        }

        /**
         * 设置图片
         *
         * @param bitmap 图片位图
         * @return [Builder]
         */
        fun setBitmap(bitmap: Bitmap): Builder {
            this.bitmap = bitmap
            imageIsBitmap = true
            return this
        }

        /**
         * 设置图片
         *
         * @param drawable 图片资源
         * @return [Builder]
         */
        fun setDrawable(drawable: Drawable): Builder {
            this.drawable = drawable
            imageIsDrawable = true
            return this
        }

        /**
         * 设置图片
         *
         * @param uri 图片uri
         * @return [Builder]
         */
        fun setUri(uri: Uri): Builder {
            this.uri = uri
            imageIsUri = true
            return this
        }

        /**
         * 设置图片
         *
         * @param resourceId 图片资源id
         * @return [Builder]
         */
        fun setResourceId(@DrawableRes resourceId: Int): Builder {
            this.resourceId = resourceId
            imageIsResourceId = true
            return this
        }

        /**
         * 设置点击事件
         *
         * 需添加view.setMovementMethod(LinkMovementMethod.getInstance())
         *
         * @param clickSpan 点击事件
         * @return [Builder]
         */
        fun setClickSpan(clickSpan: ClickableSpan): Builder {
            this.clickSpan = clickSpan
            return this
        }

        /**
         * 设置超链接
         *
         * 需添加view.setMovementMethod(LinkMovementMethod.getInstance())
         *
         * @param url 超链接
         * @return [Builder]
         */
        fun setUrl(url: String): Builder {
            this.url = url
            return this
        }

        /**
         * 设置模糊
         *
         * 尚存bug，其他地方存在相同的字体的话，相同字体出现在之前的话那么就不会模糊，出现在之后的话那会一起模糊
         *
         * 推荐还是把所有字体都模糊这样使用
         *
         * @param radius 模糊半径（需大于0）
         * @param style  模糊样式
         *  * [Blur.NORMAL]
         *  * [Blur.SOLID]
         *  * [Blur.OUTER]
         *  * [Blur.INNER]
         *
         * @return [Builder]
         */
        fun setBlur(radius: Float, style: Blur?): Builder {
            this.radius = radius
            this.style = style
            isBlur = true
            return this
        }

        /**
         * 追加样式字符串
         *
         * @param text 样式字符串文本
         * @return [Builder]
         */
        fun append(text: CharSequence): Builder {
            setSpan()
            this.text = text
            return this
        }

        /**
         * 创建样式字符串
         *
         * @return 样式字符串
         */
        fun create(): SpannableStringBuilder {
            setSpan()
            return mBuilder
        }

        fun into(textView: TextView?) {
            setSpan()
            if (textView != null) {
                textView.text = mBuilder
            }
        }

        /**
         * 设置样式
         */
        private fun setSpan() {
            val start = mBuilder.length
            mBuilder.append(text)
            val end = mBuilder.length
            if (foregroundColor != defaultValue) {
                mBuilder.setSpan(ForegroundColorSpan(foregroundColor), start, end, flag)
                foregroundColor = defaultValue
            }
            if (backgroundColor != defaultValue) {
                mBuilder.setSpan(BackgroundColorSpan(backgroundColor), start, end, flag)
                backgroundColor = defaultValue
            }
            if (isLeadingMargin) {
                mBuilder.setSpan(LeadingMarginSpan.Standard(first, rest), start, end, flag)
                isLeadingMargin = false
            }
            if (quoteColor != defaultValue) {
                mBuilder.setSpan(QuoteSpan(quoteColor), start, end, 0)
                quoteColor = defaultValue
            }
            if (isBullet) {
                mBuilder.setSpan(BulletSpan(gapWidth, bulletColor), start, end, 0)
                isBullet = false
            }
            if (proportion != -1f) {
                mBuilder.setSpan(RelativeSizeSpan(proportion), start, end, flag)
                proportion = -1f
            }
            if (xProportion != -1f) {
                mBuilder.setSpan(ScaleXSpan(xProportion), start, end, flag)
                xProportion = -1f
            }
            if (isStrikethrough) {
                mBuilder.setSpan(StrikethroughSpan(), start, end, flag)
                isStrikethrough = false
            }
            if (isUnderline) {
                mBuilder.setSpan(UnderlineSpan(), start, end, flag)
                isUnderline = false
            }
            if (isSuperscript) {
                mBuilder.setSpan(SuperscriptSpan(), start, end, flag)
                isSuperscript = false
            }
            if (isSubscript) {
                mBuilder.setSpan(SubscriptSpan(), start, end, flag)
                isSubscript = false
            }
            if (isBold) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
                isBold = false
            }
            if (isItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
                isItalic = false
            }
            if (isBoldItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
                isBoldItalic = false
            }
            if (fontFamily != null) {
                mBuilder.setSpan(TypefaceSpan(fontFamily), start, end, flag)
                fontFamily = null
            }
            if (align != null) {
                mBuilder.setSpan(AlignmentSpan.Standard(align!!), start, end, flag)
                align = null
            }
            if (imageIsBitmap || imageIsDrawable || imageIsUri || imageIsResourceId) {
                if (imageIsBitmap) {
                    mBuilder.setSpan(ImageSpan(RxTool.getContext(), bitmap!!), start, end, flag)
                    bitmap = null
                    imageIsBitmap = false
                } else if (imageIsDrawable) {
                    mBuilder.setSpan(ImageSpan(drawable!!), start, end, flag)
                    drawable = null
                    imageIsDrawable = false
                } else if (imageIsUri) {
                    mBuilder.setSpan(ImageSpan(RxTool.getContext(), uri!!), start, end, flag)
                    uri = null
                    imageIsUri = false
                } else {
                    mBuilder.setSpan(ImageSpan(RxTool.getContext(), resourceId), start, end, flag)
                    resourceId = 0
                    imageIsResourceId = false
                }
            }
            if (clickSpan != null) {
                mBuilder.setSpan(clickSpan, start, end, flag)
                clickSpan = null
            }
            if (url != null) {
                mBuilder.setSpan(URLSpan(url), start, end, flag)
                url = null
            }
            if (isBlur) {
                mBuilder.setSpan(MaskFilterSpan(BlurMaskFilter(radius, style)), start, end, flag)
                isBlur = false
            }
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        }

        init {
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            foregroundColor = defaultValue
            backgroundColor = defaultValue
            quoteColor = defaultValue
            proportion = -1f
            xProportion = -1f
            mBuilder = SpannableStringBuilder()
        }
    }
}