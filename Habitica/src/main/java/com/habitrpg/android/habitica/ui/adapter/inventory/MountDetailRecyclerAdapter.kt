package com.habitrpg.android.habitica.ui.adapter.inventory

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.habitrpg.android.habitica.R
import com.habitrpg.android.habitica.extensions.inflate
import com.habitrpg.android.habitica.extensions.notNull
import com.habitrpg.android.habitica.models.inventory.Mount
import com.habitrpg.android.habitica.ui.helpers.DataBindingUtils
import com.habitrpg.android.habitica.ui.helpers.bindView
import com.habitrpg.android.habitica.ui.menu.BottomSheetMenu
import com.habitrpg.android.habitica.ui.menu.BottomSheetMenuItem
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class MountDetailRecyclerAdapter(data: OrderedRealmCollection<Mount>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<Mount, MountDetailRecyclerAdapter.MountViewHolder>(data, autoUpdate) {

    var itemType: String? = null

    private val equipEvents = PublishSubject.create<String>()


    fun getEquipFlowable(): Flowable<String> {
        return equipEvents.toFlowable(BackpressureStrategy.DROP)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MountViewHolder {
        return MountViewHolder(parent.inflate(R.layout.animal_overview_item))
    }

    override fun onBindViewHolder(holder: MountViewHolder, position: Int) {
        data.notNull { holder.bind(it[position]) }
    }

    inner class MountViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var animal: Mount? = null

        private val imageView: SimpleDraweeView by bindView(R.id.imageView)
        private val titleView: TextView by bindView(R.id.titleTextView)
        private val ownedTextView: TextView by bindView(R.id.ownedTextView)

        var resources: Resources = itemView.resources

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: Mount) {
            animal = item
            titleView.text = item.colorText
            ownedTextView.visibility = View.GONE
            this.imageView.alpha = 1.0f
            if (this.animal?.owned == true) {
                DataBindingUtils.loadImage(this.imageView, "Mount_Icon_" + itemType + "-" + item.color)
            } else {
                DataBindingUtils.loadImage(this.imageView, "PixelPaw")
                this.imageView.alpha = 0.3f
            }
        }

        override fun onClick(v: View) {
            if (this.animal?.owned == false) {
                return
            }
            val menu = BottomSheetMenu(itemView.context)
            menu.addMenuItem(BottomSheetMenuItem(resources.getString(R.string.use_animal)))
            menu.setSelectionRunnable {
                animal.notNull { equipEvents.onNext(it.key) }
            }
            menu.show()
        }
    }
}
