package com.example.runcause;


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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.Constants;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.AdapterProject;
import com.example.runcause.model.intefaces.UploadImageListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserHomePageFragment extends Fragment {
    ListProjectFragmentViewModel viewModelProject;
    View view;
    TextView userName, email;
    User user;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToUserRunListFragment HomeToRunList;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToRunScreenFragment UserToNewRun;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToEditUserFragment UserToEditUser;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToAddRunProjectFragment UserToAddProject;
    SwipeRefreshLayout swipeRefresh;
    ImageButton editUserImg, addNewProjectBtn;
    ImageView userImage;
    ProgressBar progressBar;
    AdapterProject adapter;
    Project project;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModelProject = new ViewModelProvider(this).get(ListProjectFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_user_home_page, container, false);
        user=UserHomePageFragmentArgs.fromBundle(getArguments()).getUser();
        project=UserHomePageFragmentArgs.fromBundle(getArguments()).getProject();
        userName= view.findViewById(R.id.user_page_name_tv);
        email= view.findViewById(R.id.user_page_email_tv);
        progressBar = view.findViewById(R.id.user_page_progressbar);
        swipeRefresh = view.findViewById(R.id.user_home_page_swip_refresh);
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            Model.instance.reloadProjectList();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        });
        userImage=view.findViewById(R.id.user_page_image);
        editUserImg=view.findViewById(R.id.user_add_page_image_btn);
        addNewProjectBtn=view.findViewById(R.id.add_project_btn);
        RecyclerView list = view.findViewById(R.id.user_page_project_list_tv);
        adapter = new AdapterProject();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);
        viewModelProject.getData().observe(getViewLifecycleOwner(), projects -> {
            adapter.setFragment(UserHomePageFragment.this);
            adapter.setData(projects);
            adapter.notifyDataSetChanged();
        });
        progressBar.setVisibility(View.GONE);
        adapter.setOnItemClickListener((position, v) -> progressBar.setVisibility(View.VISIBLE));
        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(),loadingState -> {
            swipeRefresh.setRefreshing(loadingState== LoadingState.loading);
        });
        editUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhoto();
            }
        });
        updateUserPage();
        setHasOptionsMenu(true);
        return view;
    }
    private void updateUserPage() {
        userName.setText(user.getName());
        email.setText(user.getEmail());
        progressBar.setVisibility(View.GONE);
        userImage.setImageResource(R.drawable.userpage);
        if(user.getImageUrl()!=null){
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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        boolean result = true;
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.run_list_menu:
                    HomeToRunList = UserHomePageFragmentDirections.actionUserHomePageFragmentToUserRunListFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(HomeToRunList);
                    break;
                case R.id.edit_user:
                    UserToEditUser = UserHomePageFragmentDirections.actionUserHomePageFragmentToEditUserFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(UserToEditUser);
                    break;
                case R.id.home_menu:
                    UserToNewRun = UserHomePageFragmentDirections.actionUserHomePageFragmentToRunScreenFragment(user,project);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(UserToNewRun);
                    break;
                case R.id.new_project:
                    UserToAddProject = UserHomePageFragmentDirections.actionUserHomePageFragmentToAddRunProjectFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(UserToAddProject);
                    break;
                case R.id.logout_menu:
                    FirebaseAuth.getInstance().signOut();
                    Navigation.findNavController(view).navigate(R.id.action_userHomePageFragment_to_startAppFragment);
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }
}
