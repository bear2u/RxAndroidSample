package pe.kr.rxandroidsample.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import pe.kr.rxandroidsample.datas.Item;

import static pe.kr.rxandroidsample.LogUtils._log;

/**
 * Created by tommy on 2017-10-07.
 */

public class ItemModel{
    private static final int COUNT = 100;
    private static Random random = new Random();

    public static Flowable<List<Item>> latestThings(long interval , TimeUnit timeUnit) {
        return Flowable
                .interval( 0 , interval , timeUnit , Schedulers.computation())
                .map( i -> shuffle( randomItems().subList(0 , (int) (COUNT * 0.8f))));
    }

    public static List<Item> randomItems() {
        List<Item> items = new ArrayList<>(COUNT);
        for(int i=0;i<COUNT;i++){
            items.add( newItem(i));
        }
        return items;
    }

    private static List<Item> shuffle(List<Item> items) {
        List<Item> shuffled = new ArrayList<>(items.size());
        while(!items.isEmpty()){
            Item item = items.remove( random.nextInt(items.size()));
            shuffled.add(item);
        }
        return shuffled;
    }


    private static Item newItem(int id ){
        Item.Builder builder = Item.builder();

        builder.id(id);
        char first = (char)(random.nextInt(25) + 65);
        char second = (char)(random.nextInt(25) + 65);
        char third = (char)(random.nextInt(25) + 65);
        builder.text(String.valueOf(new char[]{first, second , third}));
        builder.color(random.nextInt());

        return builder.build();
    }
}