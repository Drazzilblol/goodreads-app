package com.training.dr.androidtraining.presentation.main.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.data.models.User;
import com.training.dr.androidtraining.presentation.common.events.OnFragmentLoadedListener;
import com.training.dr.androidtraining.ulils.image.ImageLoadingManager;

public class UserInfoFragment extends Fragment {
    private static final String USER = "USER";

    private View v;
    private User user;

    public UserInfoFragment() {
    }

    public static UserInfoFragment getInstance(User user) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_info, container, false);
        initArguments();
        initViews();
        return v;
    }

    private void initArguments() {
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER);
        }
    }

    private void initViews() {
        TextView nameView = (TextView) v.findViewById(R.id.user_name_view);
        nameView.setText(user.getName());
        TextView idView = (TextView) v.findViewById(R.id.user_id_view);
        idView.setText(user.getGoodreadId() + "");
        imageViewInit();
    }

    private void imageViewInit() {
        ImageView imageView = (ImageView) v.findViewById(R.id.user_avatar_image);
        ImageLoadingManager.startBuild()
                .imageUrl(user.getAvatarUrl())
                .placeholder(R.drawable.book_image_paceholder)
                .transform(500, 800)
                .load(imageView);
    }

    @Override
    public void onResume() {
        super.onResume();
        OnFragmentLoadedListener listener = (OnFragmentLoadedListener) getActivity();
        String title = getResources().getString(R.string.user_info_fragment_title);
        listener.onFragmentLoaded(title);
    }

}
