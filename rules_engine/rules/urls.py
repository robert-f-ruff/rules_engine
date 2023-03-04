"""Define the URLs used by the rules engine."""
from django.urls import path
from . import views

app_name = 'rules'
urlpatterns = [
    path('', views.IndexView.as_view(), name='index'),
    path('add/', views.RuleView.as_view(), name='add'),
    path('<int:rule_id>/', views.RuleView.as_view(), name='edit'),
    path('<int:pk>/delete/', views.RuleDeleteView.as_view(), name='delete'),
    path('parameters/<str:action_name>/', views.parameters, name='action_parameters'),
]
