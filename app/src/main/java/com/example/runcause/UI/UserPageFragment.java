package com.example.runcause.UI;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wineapp.R;
import com.example.wineapp.model.Constants;
import com.example.wineapp.model.Model;
import com.example.wineapp.model.Post;
import com.example.wineapp.model.User;
import com.example.wineapp.model.adapter.MyAdapter;
import com.example.wineapp.model.intefaces.OnItemClickListener;
import com.example.wineapp.model.intefaces.UploadImageListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class UserPageFragment extends Fragment {
    UserPageFragmentViewModel viewModel;
    View view;
    MyAdapter adapter;
    ProgressBar progressbar;
    TextView userName, email;
    User user;
    UserPageFragmentDirections.ActionUserPageFragmentToUserAddPostFragment action;
    SwipeRefreshLayout swipeRefresh;
    ImageButton photoUser;
    ImageView userImage;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(UserPageFragmentViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_page, container, false);
        user=UserPageFragmentArgs.fromBundle(getArguments()).getUser();
        viewModel.setData(user);
        userName=view.findViewById(R.id.user_page_name_tv);
        email=view.findViewById(R.id.user_page_email_tv);
        progressbar= view.findViewById(R.id.user_page_progressbar);
        progressbar.setVisibility(View.VISIBLE);
        swipeRefresh=view.findViewById(R.id.user_page_swipe_refresh);
        photoUser=view.findViewById(R.id.user_add_page_image_btn);
        userImage=view.findViewById(R.id.user_page_image);
        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhoto();

            }
        });
        RecyclerView list = view.findViewById(R.id.user_page_post_list_tv);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progressbar.setVisibility(View.VISIBLE);
                UserPageFragmentDirections.ActionUserPageFragmentToEditPostFragment action = UserPageFragmentDirections.actionUserPageFragmentToEditPostFragment(viewModel.getData().getValue().get(position),user,position);
                Navigation.findNavController(v).navigate(action);
            }
        });
        updateUserPage();
        setHasOptionsMenu(true);
        action=UserPageFragmentDirections.actionUserPageFragmentToUserAddPostFragment(user);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });
        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                adapter.setFragment(UserPageFragment.this);
                adapter.setData(viewModel.getData().getValue());
                progressbar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
    private void updateUserPage() {
        userName.setText(user.getName());
        email.setText(user.getEmail());
        progressbar.setVisibility(View.GONE);
        userImage.setImageResource(R.drawable.userpage);
        if(user.getImageUrl().length()>2){
            Picasso.get().load(user.getImageUrl()).into(userImage);
        }
    }

    private void editPhoto() {
        Intent intent = getPickImageIntent(getActivity());
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.REQUEST_IMAGE_CAPTURE);
    }
    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Pick Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras;
            Bitmap imageBitmap;
            InputStream inputStream;
            try {
                if (data.getAction() != null && data.getAction().equals("inline-data")) {
                    // take picture from camera
                    extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                } else {
                    // pick from gallery
                    inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    imageBitmap = BitmapFactory.decodeStream(inputStream);
                }
                userImage.setImageBitmap(imageBitmap);
                Model.instance.uploadImage(imageBitmap, user.getEmail(), new UploadImageListener() {
                    @Override
                    public void onComplete(String url) {
                        if (url == null) {

                        } else {
                            user.setImageUrl(url);
                            Model.instance.addUser(user, () -> {
                                return;
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_page_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result = true;
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.userPageLogout:
                    progressbar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signOut();
                    Navigation.findNavController(view).navigate(R.id.action_userPageFragment_to_startAppFragment);
                    break;
                case R.id.userPageAddPost:
                    progressbar.setVisibility(View.VISIBLE);
                    Navigation.findNavController(view).navigate(action);
                    break;
                case R.id.userPageListPost:
                    viewModel=null;
                    progressbar.setVisibility(View.VISIBLE);
                    UserPageFragmentDirections.ActionUserPageFragmentToListPostFragment action1=UserPageFragmentDirections.actionUserPageFragmentToListPostFragment(user);
                    Navigation.findNavController(view).navigate(action1);
                    break;
                case R.id.userPageMapPost:
                    viewModel=null;
                    progressbar.setVisibility(View.VISIBLE);
                    UserPageFragmentDirections.ActionUserPageFragmentToMapFragment action12=UserPageFragmentDirections.actionUserPageFragmentToMapFragment(user);
                    Navigation.findNavController(view).navigate(action12);
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }
}