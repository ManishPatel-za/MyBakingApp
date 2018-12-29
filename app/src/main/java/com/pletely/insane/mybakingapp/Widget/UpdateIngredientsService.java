package com.pletely.insane.mybakingapp.Widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class UpdateIngredientsService extends IntentService {

    public static final String FETCH_INGREDIENTS = "ingredients_list";
    public static final String ACTION_FETCH_INGREDIENTS = "com.pletely.insane.mybakingapp.action.fetch_ingredients";

    public UpdateIngredientsService() {
        super("UpdateIngredientsService");
    }

    public static void startIngredientsService(Context context, ArrayList<String> fromIngredientsList) {
        Intent intent = new Intent(context, UpdateIngredientsService.class);
        intent.putExtra(ACTION_FETCH_INGREDIENTS,fromIngredientsList);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ArrayList<String> fromIngredientsActivityList = intent.getExtras().getStringArrayList(ACTION_FETCH_INGREDIENTS);
            handleActionFetchIngredients(fromIngredientsActivityList);
        }
    }

    private void handleActionFetchIngredients(ArrayList<String> fromIngredientsList) {
        Intent intent = new Intent(ACTION_FETCH_INGREDIENTS);
        intent.setAction(ACTION_FETCH_INGREDIENTS);
        intent.putExtra(FETCH_INGREDIENTS, fromIngredientsList);
        sendBroadcast(intent);
    }
}
