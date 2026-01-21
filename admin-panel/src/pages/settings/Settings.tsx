import { useState } from 'react';
import toast from 'react-hot-toast';

interface PlatformSettings {
  restaurantCommission: number;
  deliveryFee: number;
  perKmFee: number;
  minimumOrderAmount: number;
  vatPercentage: number;
}

interface PaymentGatewaySettings {
  bkash: boolean;
  nagad: boolean;
  rocket: boolean;
  sslcommerz: boolean;
  cod: boolean;
}

interface AppSettings {
  maintenanceMode: boolean;
  allowRestaurantRegistration: boolean;
  allowRiderRegistration: boolean;
  orderAutoAssign: boolean;
  notificationsEnabled: boolean;
}

export default function Settings() {
  const [platformSettings, setPlatformSettings] = useState<PlatformSettings>({
    restaurantCommission: 15,
    deliveryFee: 50,
    perKmFee: 10,
    minimumOrderAmount: 100,
    vatPercentage: 5,
  });

  const [paymentGateways, setPaymentGateways] = useState<PaymentGatewaySettings>({
    bkash: true,
    nagad: true,
    rocket: false,
    sslcommerz: true,
    cod: true,
  });

  const [appSettings, setAppSettings] = useState<AppSettings>({
    maintenanceMode: false,
    allowRestaurantRegistration: true,
    allowRiderRegistration: true,
    orderAutoAssign: true,
    notificationsEnabled: true,
  });

  const [activeTab, setActiveTab] = useState('commission');
  const [saving, setSaving] = useState(false);

  const handleSavePlatformSettings = async () => {
    setSaving(true);
    try {
      // Simulating API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast.success('কমিশন সেটিংস সংরক্ষণ হয়েছে');
    } catch {
      toast.error('সেটিংস সংরক্ষণ ব্যর্থ হয়েছে');
    } finally {
      setSaving(false);
    }
  };

  const handleSavePaymentSettings = async () => {
    setSaving(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast.success('পেমেন্ট সেটিংস সংরক্ষণ হয়েছে');
    } catch {
      toast.error('সেটিংস সংরক্ষণ ব্যর্থ হয়েছে');
    } finally {
      setSaving(false);
    }
  };

  const handleSaveAppSettings = async () => {
    setSaving(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast.success('অ্যাপ্লিকেশন সেটিংস সংরক্ষণ হয়েছে');
    } catch {
      toast.error('সেটিংস সংরক্ষণ ব্যর্থ হয়েছে');
    } finally {
      setSaving(false);
    }
  };

  const tabs = [
    { id: 'commission', label: 'কমিশন ও ফি', icon: '💰' },
    { id: 'payment', label: 'পেমেন্ট গেটওয়ে', icon: '💳' },
    { id: 'app', label: 'অ্যাপ্লিকেশন', icon: '⚙️' },
    { id: 'profile', label: 'প্রোফাইল', icon: '👤' },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-800">সিস্টেম সেটিংস</h1>

      {/* Tabs */}
      <div className="bg-white rounded-xl shadow-sm">
        <div className="border-b">
          <nav className="flex -mb-px">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                  activeTab === tab.id
                    ? 'border-orange-500 text-orange-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <span className="mr-2">{tab.icon}</span>
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          {/* Commission Settings Tab */}
          {activeTab === 'commission' && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-800 mb-4">কমিশন ও ফি সেটিংস</h2>
                <p className="text-sm text-gray-500 mb-6">
                  প্ল্যাটফর্মের জন্য কমিশন এবং ডেলিভারি ফি কনফিগার করুন
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    রেস্টুরেন্ট কমিশন (%)
                  </label>
                  <input
                    type="number"
                    value={platformSettings.restaurantCommission}
                    onChange={(e) => setPlatformSettings({
                      ...platformSettings,
                      restaurantCommission: Number(e.target.value)
                    })}
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    min="0"
                    max="100"
                  />
                  <p className="text-xs text-gray-500 mt-1">প্রতিটি অর্ডার থেকে কমিশন</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    বেস ডেলিভারি ফি (৳)
                  </label>
                  <input
                    type="number"
                    value={platformSettings.deliveryFee}
                    onChange={(e) => setPlatformSettings({
                      ...platformSettings,
                      deliveryFee: Number(e.target.value)
                    })}
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    min="0"
                  />
                  <p className="text-xs text-gray-500 mt-1">ন্যূনতম ডেলিভারি চার্জ</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    প্রতি কিলোমিটার ফি (৳)
                  </label>
                  <input
                    type="number"
                    value={platformSettings.perKmFee}
                    onChange={(e) => setPlatformSettings({
                      ...platformSettings,
                      perKmFee: Number(e.target.value)
                    })}
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    min="0"
                  />
                  <p className="text-xs text-gray-500 mt-1">দূরত্ব ভিত্তিক অতিরিক্ত চার্জ</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    সর্বনিম্ন অর্ডার পরিমাণ (৳)
                  </label>
                  <input
                    type="number"
                    value={platformSettings.minimumOrderAmount}
                    onChange={(e) => setPlatformSettings({
                      ...platformSettings,
                      minimumOrderAmount: Number(e.target.value)
                    })}
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    min="0"
                  />
                  <p className="text-xs text-gray-500 mt-1">অর্ডার করতে সর্বনিম্ন পরিমাণ</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    ভ্যাট (%)
                  </label>
                  <input
                    type="number"
                    value={platformSettings.vatPercentage}
                    onChange={(e) => setPlatformSettings({
                      ...platformSettings,
                      vatPercentage: Number(e.target.value)
                    })}
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    min="0"
                    max="100"
                  />
                  <p className="text-xs text-gray-500 mt-1">মূল্য সংযোজন কর</p>
                </div>
              </div>

              <div className="pt-4 border-t">
                <button
                  onClick={handleSavePlatformSettings}
                  disabled={saving}
                  className="px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 disabled:opacity-50 transition-colors"
                >
                  {saving ? 'সংরক্ষণ হচ্ছে...' : 'সেটিংস সংরক্ষণ করুন'}
                </button>
              </div>
            </div>
          )}

          {/* Payment Gateway Tab */}
          {activeTab === 'payment' && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-800 mb-4">পেমেন্ট গেটওয়ে কনফিগারেশন</h2>
                <p className="text-sm text-gray-500 mb-6">
                  গ্রাহকদের জন্য উপলব্ধ পেমেন্ট পদ্ধতি পরিচালনা করুন
                </p>
              </div>

              <div className="space-y-4">
                {[
                  { key: 'bkash', name: 'bKash', description: 'মোবাইল ফিন্যান্সিয়াল সার্ভিস', color: 'pink' },
                  { key: 'nagad', name: 'Nagad', description: 'ডিজিটাল ফিন্যান্সিয়াল সার্ভিস', color: 'orange' },
                  { key: 'rocket', name: 'Rocket', description: 'DBBL মোবাইল ব্যাংকিং', color: 'purple' },
                  { key: 'sslcommerz', name: 'SSL Commerz', description: 'কার্ড পেমেন্ট গেটওয়ে', color: 'blue' },
                  { key: 'cod', name: 'Cash on Delivery', description: 'ক্যাশে পেমেন্ট', color: 'green' },
                ].map((gateway) => (
                  <div key={gateway.key} className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50">
                    <div className="flex items-center space-x-4">
                      <div className={`w-12 h-12 bg-${gateway.color}-100 rounded-lg flex items-center justify-center text-${gateway.color}-600 font-bold`}>
                        {gateway.name.charAt(0)}
                      </div>
                      <div>
                        <h3 className="font-semibold text-gray-800">{gateway.name}</h3>
                        <p className="text-sm text-gray-500">{gateway.description}</p>
                      </div>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={paymentGateways[gateway.key as keyof PaymentGatewaySettings]}
                        onChange={(e) => setPaymentGateways({
                          ...paymentGateways,
                          [gateway.key]: e.target.checked
                        })}
                        className="sr-only peer"
                      />
                      <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-orange-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-orange-600"></div>
                    </label>
                  </div>
                ))}
              </div>

              <div className="pt-4 border-t">
                <button
                  onClick={handleSavePaymentSettings}
                  disabled={saving}
                  className="px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 disabled:opacity-50 transition-colors"
                >
                  {saving ? 'সংরক্ষণ হচ্ছে...' : 'পেমেন্ট সেটিংস সংরক্ষণ করুন'}
                </button>
              </div>
            </div>
          )}

          {/* App Settings Tab */}
          {activeTab === 'app' && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-800 mb-4">অ্যাপ্লিকেশন সেটিংস</h2>
                <p className="text-sm text-gray-500 mb-6">
                  প্ল্যাটফর্মের সাধারণ কনফিগারেশন
                </p>
              </div>

              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 border rounded-lg bg-red-50 border-red-200">
                  <div>
                    <h3 className="font-semibold text-gray-800">মেইনটেন্যান্স মোড</h3>
                    <p className="text-sm text-gray-500">সাময়িকভাবে ব্যবহারকারীদের প্রবেশাধিকার বন্ধ করুন</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={appSettings.maintenanceMode}
                      onChange={(e) => setAppSettings({ ...appSettings, maintenanceMode: e.target.checked })}
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-red-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-red-600"></div>
                  </label>
                </div>

                <div className="flex items-center justify-between p-4 border rounded-lg">
                  <div>
                    <h3 className="font-semibold text-gray-800">নতুন রেস্টুরেন্ট রেজিস্ট্রেশন</h3>
                    <p className="text-sm text-gray-500">নতুন রেস্টুরেন্টদের রেজিস্ট্রেশন অনুমতি দিন</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={appSettings.allowRestaurantRegistration}
                      onChange={(e) => setAppSettings({ ...appSettings, allowRestaurantRegistration: e.target.checked })}
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-orange-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-orange-600"></div>
                  </label>
                </div>

                <div className="flex items-center justify-between p-4 border rounded-lg">
                  <div>
                    <h3 className="font-semibold text-gray-800">নতুন রাইডার রেজিস্ট্রেশন</h3>
                    <p className="text-sm text-gray-500">নতুন রাইডারদের আবেদন অনুমতি দিন</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={appSettings.allowRiderRegistration}
                      onChange={(e) => setAppSettings({ ...appSettings, allowRiderRegistration: e.target.checked })}
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-orange-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-orange-600"></div>
                  </label>
                </div>

                <div className="flex items-center justify-between p-4 border rounded-lg">
                  <div>
                    <h3 className="font-semibold text-gray-800">অটো অর্ডার অ্যাসাইনমেন্ট</h3>
                    <p className="text-sm text-gray-500">স্বয়ংক্রিয়ভাবে কাছের রাইডারকে অর্ডার বরাদ্দ করুন</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={appSettings.orderAutoAssign}
                      onChange={(e) => setAppSettings({ ...appSettings, orderAutoAssign: e.target.checked })}
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-orange-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-orange-600"></div>
                  </label>
                </div>

                <div className="flex items-center justify-between p-4 border rounded-lg">
                  <div>
                    <h3 className="font-semibold text-gray-800">পুশ নোটিফিকেশন</h3>
                    <p className="text-sm text-gray-500">ব্যবহারকারীদের নোটিফিকেশন পাঠান</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={appSettings.notificationsEnabled}
                      onChange={(e) => setAppSettings({ ...appSettings, notificationsEnabled: e.target.checked })}
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-orange-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-orange-600"></div>
                  </label>
                </div>
              </div>

              <div className="pt-4 border-t">
                <button
                  onClick={handleSaveAppSettings}
                  disabled={saving}
                  className="px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 disabled:opacity-50 transition-colors"
                >
                  {saving ? 'সংরক্ষণ হচ্ছে...' : 'অ্যাপ সেটিংস সংরক্ষণ করুন'}
                </button>
              </div>
            </div>
          )}

          {/* Profile Tab */}
          {activeTab === 'profile' && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-semibold text-gray-800 mb-4">অ্যাডমিন প্রোফাইল</h2>
                <p className="text-sm text-gray-500 mb-6">
                  আপনার প্রোফাইল তথ্য আপডেট করুন
                </p>
              </div>

              <div className="flex items-center space-x-6 mb-6">
                <div className="w-24 h-24 bg-gradient-to-br from-orange-500 to-red-500 rounded-full flex items-center justify-center text-white text-3xl font-bold">
                  A
                </div>
                <div>
                  <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors">
                    ছবি পরিবর্তন করুন
                  </button>
                  <p className="text-xs text-gray-500 mt-2">JPG, PNG সর্বোচ্চ 2MB</p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">নাম</label>
                  <input
                    type="text"
                    defaultValue="Admin User"
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">ইমেইল</label>
                  <input
                    type="email"
                    defaultValue="admin@khabarlagbe.com"
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">ফোন</label>
                  <input
                    type="tel"
                    defaultValue="+880 1712345678"
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">ভূমিকা</label>
                  <input
                    type="text"
                    value="Super Admin"
                    disabled
                    className="w-full px-4 py-2 border rounded-lg bg-gray-100 text-gray-500"
                  />
                </div>
              </div>

              <div className="pt-6 border-t">
                <h3 className="text-md font-semibold text-gray-800 mb-4">পাসওয়ার্ড পরিবর্তন</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">বর্তমান পাসওয়ার্ড</label>
                    <input
                      type="password"
                      className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">নতুন পাসওয়ার্ড</label>
                    <input
                      type="password"
                      className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">পাসওয়ার্ড নিশ্চিত করুন</label>
                    <input
                      type="password"
                      className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
                    />
                  </div>
                </div>
              </div>

              <div className="pt-4 border-t">
                <button className="px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition-colors">
                  প্রোফাইল আপডেট করুন
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
