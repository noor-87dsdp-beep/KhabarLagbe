package com.noor.khabarlagbe.rider;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.noor.khabarlagbe.rider.data.remote.api.RiderApi;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideLocationRepositoryFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideOkHttpClientFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideRetrofitFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideRiderApiFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideRiderAuthRepositoryFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideRiderEarningsRepositoryFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideRiderOrderRepositoryFactory;
import com.noor.khabarlagbe.rider.di.NetworkModule_ProvideRiderProfileRepositoryFactory;
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository;
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository;
import com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository;
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository;
import com.noor.khabarlagbe.rider.domain.repository.RiderProfileRepository;
import com.noor.khabarlagbe.rider.presentation.auth.RiderAuthViewModel;
import com.noor.khabarlagbe.rider.presentation.auth.RiderAuthViewModel_HiltModules;
import com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryViewModel;
import com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryViewModel_HiltModules;
import com.noor.khabarlagbe.rider.presentation.earnings.EarningsViewModel;
import com.noor.khabarlagbe.rider.presentation.earnings.EarningsViewModel_HiltModules;
import com.noor.khabarlagbe.rider.presentation.home.RiderHomeViewModel;
import com.noor.khabarlagbe.rider.presentation.home.RiderHomeViewModel_HiltModules;
import com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersViewModel;
import com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersViewModel_HiltModules;
import com.noor.khabarlagbe.rider.presentation.profile.RiderProfileViewModel;
import com.noor.khabarlagbe.rider.presentation.profile.RiderProfileViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerRiderApplication_HiltComponents_SingletonC {
  private DaggerRiderApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static RiderApplication_HiltComponents.SingletonC create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public RiderApplication_HiltComponents.SingletonC build() {
      return new SingletonCImpl();
    }
  }

  private static final class ActivityRetainedCBuilder implements RiderApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements RiderApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements RiderApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements RiderApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements RiderApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements RiderApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements RiderApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public RiderApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends RiderApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends RiderApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends RiderApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends RiderApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_delivery_ActiveDeliveryViewModel, ActiveDeliveryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_orders_AvailableOrdersViewModel, AvailableOrdersViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_earnings_EarningsViewModel, EarningsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_auth_RiderAuthViewModel, RiderAuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_home_RiderHomeViewModel, RiderHomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_profile_RiderProfileViewModel, RiderProfileViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_noor_khabarlagbe_rider_presentation_delivery_ActiveDeliveryViewModel = "com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryViewModel";

      static String com_noor_khabarlagbe_rider_presentation_auth_RiderAuthViewModel = "com.noor.khabarlagbe.rider.presentation.auth.RiderAuthViewModel";

      static String com_noor_khabarlagbe_rider_presentation_earnings_EarningsViewModel = "com.noor.khabarlagbe.rider.presentation.earnings.EarningsViewModel";

      static String com_noor_khabarlagbe_rider_presentation_profile_RiderProfileViewModel = "com.noor.khabarlagbe.rider.presentation.profile.RiderProfileViewModel";

      static String com_noor_khabarlagbe_rider_presentation_orders_AvailableOrdersViewModel = "com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersViewModel";

      static String com_noor_khabarlagbe_rider_presentation_home_RiderHomeViewModel = "com.noor.khabarlagbe.rider.presentation.home.RiderHomeViewModel";

      @KeepFieldType
      ActiveDeliveryViewModel com_noor_khabarlagbe_rider_presentation_delivery_ActiveDeliveryViewModel2;

      @KeepFieldType
      RiderAuthViewModel com_noor_khabarlagbe_rider_presentation_auth_RiderAuthViewModel2;

      @KeepFieldType
      EarningsViewModel com_noor_khabarlagbe_rider_presentation_earnings_EarningsViewModel2;

      @KeepFieldType
      RiderProfileViewModel com_noor_khabarlagbe_rider_presentation_profile_RiderProfileViewModel2;

      @KeepFieldType
      AvailableOrdersViewModel com_noor_khabarlagbe_rider_presentation_orders_AvailableOrdersViewModel2;

      @KeepFieldType
      RiderHomeViewModel com_noor_khabarlagbe_rider_presentation_home_RiderHomeViewModel2;
    }
  }

  private static final class ViewModelCImpl extends RiderApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ActiveDeliveryViewModel> activeDeliveryViewModelProvider;

    private Provider<AvailableOrdersViewModel> availableOrdersViewModelProvider;

    private Provider<EarningsViewModel> earningsViewModelProvider;

    private Provider<RiderAuthViewModel> riderAuthViewModelProvider;

    private Provider<RiderHomeViewModel> riderHomeViewModelProvider;

    private Provider<RiderProfileViewModel> riderProfileViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.activeDeliveryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.availableOrdersViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.earningsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.riderAuthViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.riderHomeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.riderProfileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_delivery_ActiveDeliveryViewModel, ((Provider) activeDeliveryViewModelProvider)).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_orders_AvailableOrdersViewModel, ((Provider) availableOrdersViewModelProvider)).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_earnings_EarningsViewModel, ((Provider) earningsViewModelProvider)).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_auth_RiderAuthViewModel, ((Provider) riderAuthViewModelProvider)).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_home_RiderHomeViewModel, ((Provider) riderHomeViewModelProvider)).put(LazyClassKeyProvider.com_noor_khabarlagbe_rider_presentation_profile_RiderProfileViewModel, ((Provider) riderProfileViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_noor_khabarlagbe_rider_presentation_delivery_ActiveDeliveryViewModel = "com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryViewModel";

      static String com_noor_khabarlagbe_rider_presentation_home_RiderHomeViewModel = "com.noor.khabarlagbe.rider.presentation.home.RiderHomeViewModel";

      static String com_noor_khabarlagbe_rider_presentation_orders_AvailableOrdersViewModel = "com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersViewModel";

      static String com_noor_khabarlagbe_rider_presentation_profile_RiderProfileViewModel = "com.noor.khabarlagbe.rider.presentation.profile.RiderProfileViewModel";

      static String com_noor_khabarlagbe_rider_presentation_earnings_EarningsViewModel = "com.noor.khabarlagbe.rider.presentation.earnings.EarningsViewModel";

      static String com_noor_khabarlagbe_rider_presentation_auth_RiderAuthViewModel = "com.noor.khabarlagbe.rider.presentation.auth.RiderAuthViewModel";

      @KeepFieldType
      ActiveDeliveryViewModel com_noor_khabarlagbe_rider_presentation_delivery_ActiveDeliveryViewModel2;

      @KeepFieldType
      RiderHomeViewModel com_noor_khabarlagbe_rider_presentation_home_RiderHomeViewModel2;

      @KeepFieldType
      AvailableOrdersViewModel com_noor_khabarlagbe_rider_presentation_orders_AvailableOrdersViewModel2;

      @KeepFieldType
      RiderProfileViewModel com_noor_khabarlagbe_rider_presentation_profile_RiderProfileViewModel2;

      @KeepFieldType
      EarningsViewModel com_noor_khabarlagbe_rider_presentation_earnings_EarningsViewModel2;

      @KeepFieldType
      RiderAuthViewModel com_noor_khabarlagbe_rider_presentation_auth_RiderAuthViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryViewModel 
          return (T) new ActiveDeliveryViewModel(singletonCImpl.provideRiderOrderRepositoryProvider.get(), singletonCImpl.provideLocationRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 1: // com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersViewModel 
          return (T) new AvailableOrdersViewModel(singletonCImpl.provideRiderOrderRepositoryProvider.get());

          case 2: // com.noor.khabarlagbe.rider.presentation.earnings.EarningsViewModel 
          return (T) new EarningsViewModel(singletonCImpl.provideRiderEarningsRepositoryProvider.get());

          case 3: // com.noor.khabarlagbe.rider.presentation.auth.RiderAuthViewModel 
          return (T) new RiderAuthViewModel(singletonCImpl.provideRiderAuthRepositoryProvider.get());

          case 4: // com.noor.khabarlagbe.rider.presentation.home.RiderHomeViewModel 
          return (T) new RiderHomeViewModel(singletonCImpl.provideRiderAuthRepositoryProvider.get(), singletonCImpl.provideRiderOrderRepositoryProvider.get(), singletonCImpl.provideRiderProfileRepositoryProvider.get());

          case 5: // com.noor.khabarlagbe.rider.presentation.profile.RiderProfileViewModel 
          return (T) new RiderProfileViewModel(singletonCImpl.provideRiderProfileRepositoryProvider.get(), singletonCImpl.provideRiderAuthRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends RiderApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends RiderApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends RiderApplication_HiltComponents.SingletonC {
    private final SingletonCImpl singletonCImpl = this;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<RiderApi> provideRiderApiProvider;

    private Provider<RiderOrderRepository> provideRiderOrderRepositoryProvider;

    private Provider<LocationRepository> provideLocationRepositoryProvider;

    private Provider<RiderEarningsRepository> provideRiderEarningsRepositoryProvider;

    private Provider<RiderAuthRepository> provideRiderAuthRepositoryProvider;

    private Provider<RiderProfileRepository> provideRiderProfileRepositoryProvider;

    private SingletonCImpl() {

      initialize();

    }

    @SuppressWarnings("unchecked")
    private void initialize() {
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 2));
      this.provideRiderApiProvider = DoubleCheck.provider(new SwitchingProvider<RiderApi>(singletonCImpl, 1));
      this.provideRiderOrderRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RiderOrderRepository>(singletonCImpl, 0));
      this.provideLocationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LocationRepository>(singletonCImpl, 4));
      this.provideRiderEarningsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RiderEarningsRepository>(singletonCImpl, 5));
      this.provideRiderAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RiderAuthRepository>(singletonCImpl, 6));
      this.provideRiderProfileRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RiderProfileRepository>(singletonCImpl, 7));
    }

    @Override
    public void injectRiderApplication(RiderApplication riderApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository 
          return (T) NetworkModule_ProvideRiderOrderRepositoryFactory.provideRiderOrderRepository(singletonCImpl.provideRiderApiProvider.get());

          case 1: // com.noor.khabarlagbe.rider.data.remote.api.RiderApi 
          return (T) NetworkModule_ProvideRiderApiFactory.provideRiderApi(singletonCImpl.provideRetrofitProvider.get());

          case 2: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 3: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 4: // com.noor.khabarlagbe.rider.domain.repository.LocationRepository 
          return (T) NetworkModule_ProvideLocationRepositoryFactory.provideLocationRepository(singletonCImpl.provideRiderApiProvider.get());

          case 5: // com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository 
          return (T) NetworkModule_ProvideRiderEarningsRepositoryFactory.provideRiderEarningsRepository(singletonCImpl.provideRiderApiProvider.get());

          case 6: // com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository 
          return (T) NetworkModule_ProvideRiderAuthRepositoryFactory.provideRiderAuthRepository(singletonCImpl.provideRiderApiProvider.get());

          case 7: // com.noor.khabarlagbe.rider.domain.repository.RiderProfileRepository 
          return (T) NetworkModule_ProvideRiderProfileRepositoryFactory.provideRiderProfileRepository(singletonCImpl.provideRiderApiProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
