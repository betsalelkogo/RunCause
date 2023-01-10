package com.example.runcause;


import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.UI.UserProjectListFragmentViewModel;
import com.example.runcause.model.Constants;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.AdapterProject;
import com.example.runcause.model.intefaces.AddUserListener;
import com.example.runcause.model.intefaces.OnItemClickListener;
import com.example.runcause.model.intefaces.UploadImageListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserHomePageFragment extends Fragment {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    EditText name, email_et, bYear, weight, height;
    Button saveEditUser, cancelEditUser,runBtn,statisticsBtn ;
    UserProjectListFragmentViewModel viewModelProject;
    View view;
    TextView userName, email;
    User user;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToUserRunListFragment HomeToRunList;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToRunScreenFragment UserToNewRun;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentSelf UserToEditUser;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToAddRunProjectFragment UserToAddProject;
    SwipeRefreshLayout swipeRefresh;
    ImageButton editUserImg;
    ImageView userImage;
    ProgressBar progressBar;
    AdapterProject adapter;
    Project project;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModelProject = new ViewModelProvider(this).get(UserProjectListFragmentViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_home_page, container, false);
        user = UserHomePageFragmentArgs.fromBundle(getArguments()).getUser();
        project = UserHomePageFragmentArgs.fromBundle(getArguments()).getProject();
        userName = view.findViewById(R.id.user_page_name_tv);
        email = view.findViewById(R.id.user_page_email_tv);
        progressBar = view.findViewById(R.id.user_page_progressbar);
        swipeRefresh = view.findViewById(R.id.user_home_page_swip_refresh);
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            Model.instance.reloadProjectList(user);
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        });
        userImage = view.findViewById(R.id.user_page_image);
        editUserImg = view.findViewById(R.id.user_add_page_image_btn);
        RecyclerView list = view.findViewById(R.id.user_page_project_list_tv);
        adapter = new AdapterProject();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        viewModelProject.getData().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> posts) {
                adapter.setFragment(UserHomePageFragment.this);
                adapter.setData(viewModelProject.getData().getValue());
                adapter.notifyDataSetChanged();
            }
        });
        progressBar.setVisibility(View.GONE);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(user.isHaveProject()){
                    openStatistics(position);
                }

                else{
                    if (viewModelProject.getData().getValue().get(position).isDone()) {
                        Toast.makeText(MyApplication.getContext(),"This Project is COMPLETED",Toast.LENGTH_LONG).show();


                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        UserToNewRun = UserHomePageFragmentDirections.actionUserHomePageFragmentToRunScreenFragment(user, viewModelProject.getData().getValue().get(position));
                        Navigation.findNavController(v).navigate(UserToNewRun);
                    }
                }
            }


        });
        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue() == LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == LoadingState.loading);
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

    private void openStatistics(int position) {
        dialogBuilder=new AlertDialog.Builder(getContext());
        final View contactPopupView = getLayoutInflater().inflate(R.layout.user_statistics_popup,null);
        runBtn=contactPopupView.findViewById(R.id.run_btn);
        statisticsBtn=contactPopupView.findViewById(R.id.statistic_btn);
        dialogBuilder.setView(contactPopupView);
        dialog=dialogBuilder.create();
        dialog.show();
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (viewModelProject.getData().getValue().get(position).isDone()) {
                    Toast.makeText(MyApplication.getContext(),"This Project is COMPLETED",Toast.LENGTH_LONG).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    UserToNewRun = UserHomePageFragmentDirections.actionUserHomePageFragmentToRunScreenFragment(user, viewModelProject.getData().getValue().get(position));
                    Navigation.findNavController(view).navigate(UserToNewRun);
                }
                dialog.dismiss();

            }
        });

        statisticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                UserHomePageFragmentDirections.ActionUserHomePageFragmentToUserStatistcProjectFragment action =UserHomePageFragmentDirections.actionUserHomePageFragmentToUserStatistcProjectFragment(user,viewModelProject.getData().getValue().get(position));
                Navigation.findNavController(view).navigate(action);
                dialog.dismiss();
            }
        });
    }
    private void updateUserPage() {
        userName.setText(user.getName());
        email.setText(user.getEmail());
        progressBar.setVisibility(View.GONE);
        userImage.setImageResource(R.drawable.userpage);
        if (user.getImageUrl() != null) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
                    createNewContactDialog();

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

    private void update() {
        name.setText(user.getName());
        email_et.setText(user.getEmail());
        bYear.setText(user.getbYear());
        weight.setText(user.getWeight());
        height.setText(user.getHeight());
    }

    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        saveEditUser.setEnabled(false);
        cancelEditUser.setEnabled(false);
        user.setName(name.getText().toString());
        user.setEmail(email_et.getText().toString());
        user.setbYear(bYear.getText().toString());
        user.setWeight(weight.getText().toString());
        user.setHeight(height.getText().toString());
        Model.instance.addUser(user, new AddUserListener() {
            @Override
            public void onComplete() {
                UserToEditUser = UserHomePageFragmentDirections.actionUserHomePageFragmentSelf(user, project);
                Navigation.findNavController(view).navigate(UserToEditUser);
            }
        });
    }

    public void createNewContactDialog() {
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View contactPopupView = getLayoutInflater().inflate(R.layout.fragment_edit_user, null);
        saveEditUser = contactPopupView.findViewById(R.id.edit_save_btn);
        cancelEditUser = contactPopupView.findViewById(R.id.edit_cancel_btn);
        name = contactPopupView.findViewById(R.id.edit_user_name_et);
        email_et = contactPopupView.findViewById(R.id.edit_user_email_et);
        bYear = contactPopupView.findViewById(R.id.edit_bYear_et);
        weight = contactPopupView.findViewById(R.id.edit_weight_et);
        height = contactPopupView.findViewById(R.id.edit_height_et);
        update();
        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();


        saveEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                save();
                dialog.dismiss();
            }
        });

        cancelEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


}
